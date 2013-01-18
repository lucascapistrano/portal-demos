Ext.define('chat.controller.ChatController', {
	extend: 'Deft.mvc.ViewController',

	control: {
		connectButton: {
			click: 'onConnectButton'
		},
		usernameTf: true,
		connectedUsersGrid: true,
		messageTf: true,
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
			open: function() {
			},
			close: function() {
				me.getUsernameTf().setDisabled(false);
				me.getConnectButton().setText('Connect');
			},
			connectedUsers: function(data) {
				me.getConnectedUsersGrid().getStore().add(data);
			},
			connected: function(data) {					
				me.getConnectedUsersGrid().getStore().add(data);
				me.getMessageTf().setDisabled(false);
				me.getSendButton().setDisabled(false);
			},
			message: function(data) {
				me.getChatView().getStore().add(data);
			}
		});
		
		var size = this.getLocalVideo().getSize();
		
		getUserMedia ({video:true, audio:true}, function(localMediaStream) {
			
	        var cfg = {
	            tag   : 'video',
	            width : size.width,
	            height: size.height,
	            src: window.URL.createObjectURL(localMediaStream),
	            autoplay: 1
	        };

	        me.video = me.getLocalVideo().body.createChild(cfg);

		}, function(e) {
			console.log('Reject', e);
		});

	},
	
	onResize: function() {		
		if (this.video) {
			var size = this.getLocalVideo().getSize();
			this.video.set({width: size.width, height: size.height});
		}
	},
	
	onConnectButton: function() {
		var me = this;

		if (me.connected) {
			portal.finalize();
			
			var userStore = me.getConnectedUsersGrid().getStore();
			var record = userStore.getById(me.connected);
			userStore.remove(record);
			
			me.connected = null;

			
			me.getMessageTf().setDisabled(true);
			me.getSendButton().setDisabled(true);
			
		} else {
			var username = me.getUsernameTf().getValue();
			me.connected = username;	
			me.getConnectButton().setText('Disconnect');
			me.getUsernameTf().setDisabled(true);
			portal.find().send('connect', {username: username, browser: navigator.userAgent});	
		}

	},
	
	onSendButton: function() {
		var messageTextField = this.getMessageTf();
		var text = Ext.String.trim(messageTextField.getValue());
		if (text) {
			portal.find().send('message', {message: text, username: this.connected});
		}		
		messageTextField.setValue('');
	}

});