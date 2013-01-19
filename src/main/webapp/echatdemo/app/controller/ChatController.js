Ext.define('chat.controller.ChatController', {
	extend: 'Deft.mvc.ViewController',

	control: {
		connectButton: {
			click: 'onConnectButton'
		},
		usernameTf: {
			keypress: function(tf, e) {
				if (e.getCharCode() == 13) {
					e.stopEvent();
					this.onConnectButton();
				}
				return false;
			},
			change: function(tf, value) {
				if (value) {
					this.getConnectButton().setDisabled(false);
				} else {
					this.getConnectButton().setDisabled(true);
				}
			}
		},
		connectedUsersGrid: true,
		messageTf: {
			keypress: function(txt, e) {
				if (e.getCharCode() == 13) {
					e.stopEvent();
					this.onSendButton();
				}
				return false;
			}
		},
		chatView: true,
		sendButton: {
			click: 'onSendButton'
		},
		localVideo: {
			resize: 'onResize'
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
			}
		});

		var size = this.getLocalVideo().getSize();

		getUserMedia({
			video: true,
			audio: true
		}, function(localMediaStream) {

			var cfg = {
				tag: 'video',
				width: size.width,
				height: size.height,
				src: window.URL.createObjectURL(localMediaStream),
				autoplay: 1
			};

			me.video = me.getLocalVideo().body.createChild(cfg);

		}, function(e) {
			console.log('Reject', e);
		});

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

	onResize: function() {
		if (this.video) {
			var size = this.getLocalVideo().getSize();
			this.video.set({
				width: size.width,
				height: size.height
			});
		}
	}

});