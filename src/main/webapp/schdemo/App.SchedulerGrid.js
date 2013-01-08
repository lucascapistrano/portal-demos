Ext.define('App.SchedulerGrid', {
	extend: 'Sch.panel.SchedulerGrid',
	plugins: [ Ext.create('Ext.grid.plugin.CellEditing', {
		clicksToEdit: 1
	}) ],

	initComponent: function() {
		this.callParent(arguments);

		this.socket.owner = this;

		// Add event listeners to store operations
		this.resourceStore.on('add', this.socket.doAdd, this.socket, {
			storeType: 'resource'
		});
		this.resourceStore.on('update', this.socket.doUpdate, this.socket, {
			storeType: 'resource'
		});
		this.resourceStore.on('remove', this.socket.doRemove, this.socket, {
			storeType: 'resource'
		});

		this.eventStore.on('add', this.socket.doAdd, this.socket, {
			storeType: 'event'
		});
		this.eventStore.on('update', this.socket.doUpdate, this.socket, {
			storeType: 'event'
		});
		this.eventStore.on('remove', this.socket.doRemove, this.socket, {
			storeType: 'event'
		});

		// Load initial data to Store from Server
		this.socket.doInitialLoad('resource');
		this.socket.doInitialLoad('event');
	}
});