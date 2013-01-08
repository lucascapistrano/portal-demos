Ext.define('App.Socket', {
	extend: 'Ext.util.Observable',
	owner: null,

	constructor: function(config) {

		var that = this;
		
		var transports = ["sse", "stream", "longpoll"];
		if (!window.location.search || window.location.search.indexOf('nows') === -1) {
			transports.unshift("ws");
		}
		
		portal.open("../sch", {transports: transports}).on({
			open: function() {
				Ext.get('transport').setHTML('Transport: ' + this.data("transport"));
			},
			server_doInitialLoad: function(data) {
				that.onInitialLoad(data);
			},
			server_doUpdate: function(data) {
				that.onUpdate(data);
			},
			server_doAdd: function(data) {
				that.onAdd(data);
			},
			server_syncId: function(data) {
				that.syncId(data);
			},
			server_doRemove: function(data) {
				that.onRemove(data);
			}
		});
	},

	/**
	 * On adding records to client store, send event to server and add items to DB.
	 */
	doAdd: function(store, records, index, opts) {
		var recordsData = [];

		if (records.length) {
			for ( var i = 0, l = records.length; i < l; i += 1) {
				if (opts.storeType === 'event') {
					records[i].data.Name = 'New Assignment';
				}
				recordsData.push({
					record: records[i].data,
					internalId: records[i].internalId
				});
			}
		} else {
			recordsData.push({
				record: records.data,
				internalId: records.interalId
			});
		}

		portal.find().send('client_doAdd', {
			records: recordsData,
			storeType: opts.storeType
		});
	},

	/**
	 * On adding records to DB event is received and records are added to the client store
	 */
	onAdd: function(data) {
		var storeType = data.storeType, records = data.records, store = this.getStoreByType(storeType);
		store.suspendEvents();
		
		for ( var i = 0, l = records.length; i < l; i += 1) {
			store.insert(store.getCount(), new store.model(records[i]));
		}
		
		store.resumeEvents();		
		this.owner.getView().refreshKeepingScroll();
	},

	/**
	 * On adding records to DB, sync event is received with records assigned ID's. Data should be synced with client
	 * store.
	 */
	syncId: function(data) {
		var storeType = data.storeType, ids = data.ids, store = this.getStoreByType(storeType), records = store
				.getRange();

		store.suspendEvents();
		for ( var i = 0, l = ids.length; i < l; i += 1) {
			newId = ids[i].id;
			internalId = ids[i].internalId;			

			Ext.Array.each(records, function(rec, idx) {
				if (rec.internalId == internalId) {
					rec.set('Id', newId);
					return false;
				}
			});
		}
		store.resumeEvents();
	},

	/**
	 * On updating records in client store, send event to server and update items in DB.
	 */
	doUpdate: function(store, records, type, changes, opts) {
		var recordsData = [];

		if (records.length) {
			for ( var i = 0, l = records.length; i < l; i += 1) {
				recordsData.push(records[i].data);
			}
		} else {
			recordsData.push(records.data);
		}

		portal.find().send('client_doUpdate', {
			records: recordsData,
			storeType: opts.storeType
		});
	},

	/**
	 * On updating records in DB event is received and data in client store is updated.
	 */
	onUpdate: function(data) {
		var storeType = data.storeType, records = data.records, store = this.getStoreByType(storeType), record, current;

		store.suspendEvents();

		for ( var i = 0, l = records.length; i < l; i += 1) {
			current = records[i];
			record = store.getById(current.Id);
			if (record) {
				record.set(current);
			}
		}

		this.owner.getView().refreshKeepingScroll();
		store.resumeEvents();
	},

	/**
	 * On adding removing records from client store, send event to server and remove items from DB.
	 */
	doRemove: function(store, records, index, opts) {
		var ids = [];

		if (records.length) {
			for ( var i = 0, l = records.length; i < l; i += 1) {
				ids.push(records[i].get('Id'));
			}
		} else {
			ids.push(records.get('Id'));
		}

		portal.find().send('client_doRemove', {
			ids: ids,
			storeType: opts.storeType
		});
	},

	/**
	 * On removing records from DB event is received and elements are deleted from client store.
	 */
	onRemove: function(data) {
		var storeType = data.storeType, ids = data.ids, store = this.getStoreByType(storeType), record, current;

		store.suspendEvents(true);

		for ( var i = 0; i < ids.length; i += 1) {
			current = ids[i];
			record = store.getById(current);

			store.remove(record);
		}

		store.resumeEvents();
	},

	/**
	 * On loading data from DB event is received, and data is loaded to client store.
	 */
	onInitialLoad: function(data) {
		var storeType = data.storeType, data = data.data, store = this.getStoreByType(storeType);

		store.loadData(data);
	},

	/**
	 * Emit event to server in order to receive initial data for store from the DB.
	 */
	doInitialLoad: function(storeType) {
		portal.find().send('client_doInitialLoad', {
			storeType: storeType
		});
	},

	/**
	 * Select either Resource or Event store
	 */
	getStoreByType: function(storeType) {
		if (storeType === 'resource') {
			return this.owner.resourceStore;
		} else if (storeType === 'event') {
			return this.owner.eventStore;
		}
		return null;
	}
});