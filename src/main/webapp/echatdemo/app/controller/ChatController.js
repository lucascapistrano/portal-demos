Ext.define('chat.controller.ChatController', {
	extend: 'Deft.mvc.ViewController',

	control: {
		connectButton: {
			click: 'onConnectButton'
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
		localVideo: {
			resize: 'onLocalVideoResize'
		},
		remoteVideo: {
			resize: 'onRemoteVideoResize'
		}
	},

	connected: null,

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
			receiveSdp: Ext.bind(me.receiveSdp, me),
			receiveIceCandidate: Ext.bind(me.receiveIceCandidate, me)
		});

		getUserMedia({
			video: true,
			audio: true
		}, Ext.bind(me.onUserMediaSuccess, me), Ext.bind(me.onUserMediaFailure, me));

	},

	onConnectButton: function() {
		var me = this;

		if (me.connected) {
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
			this.onConnectButton();
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
		if (this.connected && selected && selected.length > 0 && selected[0].get('username') !== this.connected) {
			this.getStartPeerConnectionButton().setDisabled(false);
		} else {
			this.getStartPeerConnectionButton().setDisabled(true);
		}
	},
	
	onUserMediaSuccess: function(localMediaStream) {
		this.localMediaStream = localMediaStream;
		
		var size = this.getLocalVideo().getSize();
		var cfg = {
			tag: 'video',
			width: size.width,
			height: size.height,
			src: window.URL.createObjectURL(localMediaStream),
			autoplay: 1
		};

		this.localVideoElement = this.getLocalVideo().body.createChild(cfg);
	},

	onUserMediaFailure: function(e) {
		console.log('Reject', e);
	},
	
	onStartPeerConnectionButton: function() {		
		this.isCaller = true;
		this.pc = new webkitRTCPeerConnection(undefined, undefined);
		this.pc.addStream(this.localMediaStream);
		var to = this.getConnectedUsersGrid().getSelectionModel().getSelection()[0].get('username');
		this.pc.createOffer(Ext.bind(this.sendSdp, this, [this.connected, to], true));
	},
	
	sendSdp: function(sdp, from, to) {
		sdp.fromUsername = from;
		sdp.toUsername = to;
		this.pc.setLocalDescription(sdp);				
		portal.find().send('sendSdp', sdp);
	},
	
	receiveSdp: function(sdp) {
		if (!this.isCaller) {
			this.isCaller = false;
			this.pc = new webkitRTCPeerConnection(undefined, undefined);
			this.pc.addStream(this.localMediaStream);	
		}
		
		this.pc.onicecandidate = Ext.bind(this.onIceCandidate, this, [sdp.fromUsername], true);
		this.pc.onaddstream = Ext.bind(this.onAddStream, this);	
		
		this.pc.setRemoteDescription(new RTCSessionDescription(sdp));
		
		if (!this.isCaller) {
			this.pc.createAnswer(Ext.bind(this.sendSdp, this, [this.connected, sdp.fromUsername], true));
		}
	},

	onIceCandidate: function(candidate, to) {
		if (candidate.candidate) {
			candidate.candidate.toUsername = to;
			portal.find().send('sendIceCandidate', candidate.candidate);
		}
	},
	
	receiveIceCandidate: function(candidate) {
		this.pc.addIceCandidate(new RTCIceCandidate(candidate));
	},

	onAddStream: function(event) {
		var size = this.getRemoteVideo().getSize();
		var cfg = {
			tag: 'video',
			width: size.width,
			height: size.height,
			src: window.URL.createObjectURL(event.stream),
			autoplay: 1
		};

		this.remoteVideoElement = this.getRemoteVideo().body.createChild(cfg);				
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
	},

	onLocalVideoResize: function() {
		if (this.localVideoElement) {
			var size = this.getLocalVideo().getSize();
			this.localVideoElement.set({
				width: size.width,
				height: size.height
			});
		}
	},
	
	onRemoteVideoResize: function() {
		if (this.remoteVideoElement) {
			var size = this.getRemoteVideo().getSize();
			this.remoteVideoElement.set({
				width: size.width,
				height: size.height
			});
		}
	},

});