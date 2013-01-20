Ext.define('chat.controller.ChatController', {
	extend: 'Deft.mvc.ViewController',

	control: {
		connectButton: {
			click: 'onConnectButtonClick'
		},
		usernameTf: {
			keypress: 'onUsernameTfKeypress',
			change: 'onUsernameChange'
		},
		connectedUsersGrid: {
			selectionchange: 'onUserSelectionChange'
		},
		startPeerConnectionButton: {
			click: 'onStartPeerConnectionButton'
		},
		messageTf: {
			keypress: 'onMessageTfKeypress'
		},
		chatView: true,
		sendButton: {
			click: 'onSendButton'
		},
		localVideo: true,
		remoteVideo: true
	},

	connected: null,

	pcConfig: {
		"iceServers": [ {
			"url": "stun:stun.stunprotocol.org:3478"
		} ]
	},

	pcConstraints: {
		"optional": []
	},

	init: function() {
		var me = this;

		var transports = [ "sse", "stream", "longpoll" ];
		if (!window.location.search || window.location.search.indexOf('nows') === -1) {
			transports.unshift("ws");
		}

		portal.open("../echat", {
			transports: transports,
			sharing: false
		}).on({
			connectedUsers: function(data) {
				me.getConnectedUsersGrid().getStore().add(data);
			},
			connected: function(data) {
				me.getConnectedUsersGrid().getStore().add(data);
			},
			disconnected: function(data) {
				var userStore = me.getConnectedUsersGrid().getStore();
				var record = userStore.getById(data.username);
				userStore.remove(record);
			},
			message: function(data) {
				me.getChatView().getStore().add(data);
				me.getChatView().up('panel').body.scroll('b', 1000, true);
			},
			snapshot: function(data) {
				var userStore = me.getConnectedUsersGrid().getStore();
				var record = userStore.getById(data.username);
				record.set('image', data.image);
				record.commit();
			},
			receiveSdp: me.receiveSdp.bind(me),
			receiveIceCandidate: me.receiveIceCandidate.bind(me)
		});

		navigator.getUserMedia({
			video: true,
			audio: !!navigator.webkitGetUserMedia
		}, me.onUserMediaSuccess.bind(me), me.onUserMediaFailure.bind(me));

		me.snapshotCanvas = document.querySelector('#snapshotCanvas');
		me.snapshotContext = me.snapshotCanvas.getContext('2d');

	},

	onConnectButtonClick: function() {
		var me = this;

		if (me.connected) {
			me.stopSnapshotTask();
			portal.find().send('disconnect', null, function() {
				me.getUsernameTf().setDisabled(false);

				me.getUsernameTf().setValue('');
				me.getConnectButton().setText('Connect');
				me.getConnectButton().setDisabled(true);

				me.getMessageTf().setDisabled(true);
				me.getSendButton().setDisabled(true);
				me.connected = null;
			});
		} else {
			var username = me.getUsernameTf().getValue();
			var userStore = me.getConnectedUsersGrid().getStore();
			if (username && !userStore.getById(username)) {
				portal.find().send('connect', {
					username: username,
					browser: navigator.userAgent
				}, function() {
					me.getMessageTf().setDisabled(false);
					me.getSendButton().setDisabled(false);

					me.connected = username;
					me.getConnectButton().setText('Disconnect');
					me.getUsernameTf().setDisabled(true);
					me.startSnapshotTask();
				});
			} else {
				Ext.Msg.show({
					title: 'Error',
					msg: 'Username ' + me.getUsernameTf().getValue() + ' already taken',
					buttons: Ext.Msg.OK,
					icon: Ext.window.MessageBox.ERROR
				});
			}
		}
	},

	onUsernameTfKeypress: function(tf, e) {
		if (e.getCharCode() == 13) {
			e.stopEvent();
			this.onConnectButtonClick();
		}
		return false;
	},

	onMessageTfKeypress: function(txt, e) {
		if (e.getCharCode() == 13) {
			e.stopEvent();
			this.onSendButton();
		}
		return false;
	},

	onUsernameChange: function(tf, value) {
		if (value) {
			this.getConnectButton().setDisabled(false);
		} else {
			this.getConnectButton().setDisabled(true);
		}
	},

	onUserSelectionChange: function(grid, selected) {
		if (!!navigator.getUserMedia && this.connected && selected && selected.length > 0
				&& selected[0].get('username') !== this.connected) {
			this.getStartPeerConnectionButton().setDisabled(false);
		} else {
			this.getStartPeerConnectionButton().setDisabled(true);
		}
	},

	onUserMediaSuccess: function(localMediaStream) {
		this.localMediaStream = localMediaStream;

		var cfg = {
			tag: 'video',
			width: '100%',
			height: '100%',
			autoplay: 1
		};

		this.localVideoElement = this.getLocalVideo().body.createChild(cfg);
		this.attachStream(this.localVideoElement.dom, this.localMediaStream);
	},

	attachStream: function(video, stream) {
		if (navigator.mozGetUserMedia) {
			video.mozSrcObject = stream;
			video.play();
		} else {
			video.src = window.URL.createObjectURL(stream);
		}
	},

	startSnapshotTask: function() {
		var me = this;
		var task = {
			run: function() {
				if (me.connected) {
					var imageData = me.takeSnapshot();
					if (imageData) {
						portal.find().send("snapshot", imageData);
					}
				}
			},
			interval: 30000
		};
		Ext.TaskManager.start(task);
	},
	
	stopSnapshotTask: function() {
		Ext.TaskManager.stopAll();
	},

	takeSnapshot: function() {
		if (this.localVideoElement) {
			var video = this.localVideoElement.dom;
			var canvas = this.snapshotCanvas; 
			canvas.setAttribute('width', video.videoWidth);
			canvas.setAttribute('height', video.videoHeight);
			this.snapshotContext.drawImage(video, 0, 0);
		    return canvas.toDataURL('image/png');
		}
		return null;
	},
	
	onUserMediaFailure: function(e) {
		console.log('Reject', e);
	},

	onStartPeerConnectionButton: function() {
		this.isCaller = true;
		this.pc = new RTCPeerConnection(this.pcConfig, this.pcConstraints);
		this.pc.addStream(this.localMediaStream);
		var to = this.getConnectedUsersGrid().getSelectionModel().getSelection()[0].get('username');
		this.pc.createOffer(this.sendSdp.bind(this, this.connected, to));
	},

	sendSdp: function(from, to, sdp) {
		sdp.fromUsername = from;
		sdp.toUsername = to;
		this.pc.setLocalDescription(sdp);
		portal.find().send('sendSdp', sdp);
	},

	receiveSdp: function(sdp) {
		if (!this.isCaller) {
			this.isCaller = false;
			this.pc = new RTCPeerConnection(this.pcConfig, this.pcConstraints);
			this.pc.addStream(this.localMediaStream);
		}

		this.pc.onicecandidate = this.onIceCandidate.bind(this, sdp.fromUsername);
		this.pc.onaddstream = this.onAddStream.bind(this);

		this.pc.setRemoteDescription(new RTCSessionDescription(sdp));

		if (!this.isCaller) {
			this.pc.createAnswer(this.sendSdp.bind(this, this.connected, sdp.fromUsername));
		}
	},

	onIceCandidate: function(to, candidate) {
		if (candidate.candidate) {
			candidate.candidate.toUsername = to;
			portal.find().send('sendIceCandidate', candidate.candidate);
		}
	},

	receiveIceCandidate: function(candidate) {
		this.pc.addIceCandidate(new RTCIceCandidate(candidate));
	},

	onAddStream: function(event) {
		var cfg = {
			tag: 'video',
			width: '100%',
			height: '100%',
			autoplay: 1
		};

		this.remoteVideoElement = this.getRemoteVideo().body.createChild(cfg);
		this.attachStream(this.remoteVideoElement.dom, event.stream);
	},

	onSendButton: function() {
		var messageTextField = this.getMessageTf();
		var text = Ext.String.trim(messageTextField.getValue());
		if (text) {
			portal.find().send('message', {
				message: text,
				username: this.connected
			});
		}
		messageTextField.setValue('');
	}

});