Ext.onReady(function() {
	Ext.QuickTips.init();

	var transports = [ "sse", "stream", "longpoll" ];
	if (!window.location.search || window.location.search.indexOf('nows') === -1) {
		transports.unshift("ws");
	}

	portal.open("../grid", {
		transports: transports,
		sharing: false
	});

	Ext.define('Book', {
		extend: 'Ext.data.Model',
		fields: [ {
			name: 'id',
			type: 'int'
		}, {
			name: 'title',
			type: 'string'
		}, {
			name: 'publisher',
			type: 'string'
		} ],
		proxy: {
			type: 'portal',
			api: {
				create: 'bookCreate',
				update: 'bookUpdate', 
				destroy: 'bookDestroy',
				read: 'bookRead',
				created: 'bookCreated',
				updated: 'bookUpdated',
				destroyed: 'bookDestroyed'
			}
		}
	});

	var store = Ext.create('Ext.data.PortalStore', {
		model: 'Book',
		autoLoad: true,
		remoteSort: true
	});

	
	var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
		clicksToMoveEditor: 1,
		autoCancel: false
	});

	var grid = Ext.create('Ext.grid.Panel', {
		store: store,
		columns: [ {
			header: 'ID',
			dataIndex: 'id',
			width: 100			
		}, {
			header: 'Title',
			dataIndex: 'title',
			flex: 1,
			editor: {
				allowBlank: false
			}
		}, {
			header: 'Publisher',
			dataIndex: 'publisher',
			flex: 1,
			editor: {
				allowBlank: true
			}
		}],
		renderTo: Ext.getBody(),
		width: 700,
		height: 400,
		title: 'Books',
		frame: true,
		tbar: [ {
			text: 'Add Book',
			handler: function() {
				rowEditing.cancelEdit();

				var r = Ext.create('Book', {
					title: 'NewTitle',
					publisher: 'NewPublisher'
				});

				store.insert(0, r);
				rowEditing.startEdit(0, 0);
			}
		}, {
			itemId: 'removeBook',
			text: 'Remove Book',
			handler: function() {
				var sm = grid.getSelectionModel();
				rowEditing.cancelEdit();
				store.remove(sm.getSelection());
				if (store.getCount() > 0) {
					sm.select(0);
				}
			},
			disabled: true
		}, '->', {
			text: 'Rollback',
			handler: function() {
				store.rejectChanges();
			}			
		}, '-', {
			text: 'Sync',
			handler: function() {
				store.sync();
			}
		} ],
		plugins: [ rowEditing ],
		listeners: {
			'selectionchange': function(view, records) {
				grid.down('#removeBook').setDisabled(!records.length);
			}
		}
	});	
		

});
