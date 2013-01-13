Ext.ns('App');

Ext.onReady(function() {	
	Ext.QuickTips.init();
	App.SchedulerDemo.init();
});

App.SchedulerDemo = {

	// Initialize application
	init: function() {
		Ext.define('App.CustomResource', {
			extend: 'Sch.model.Resource',
			fields: [ {
				name: 'FavoriteColor'
			} ]
		});

		scheduler = Ext.create('App.SchedulerGrid', {
			title: 'Portal.js/Atmosphere example',
			eventBarTextField: 'Name',
			viewPreset: 'hourAndDay',
			startDate: new Date(2013, 0, 7, 8),
			endDate: new Date(2013, 0, 7, 19),
			renderTo: 'scheduler',
			height: 350,
			width: '100%',

			socket: Ext.create('App.Socket'),
			split: true,
			tbar: [ {
				text: 'Add new row',
				handler: function() {
					scheduler.resourceStore.insert(0, new scheduler.resourceStore.model({
						Name: 'New resource',
						FavoriteColor: 'black'
					}));
				}
			} ],

			columns: [ {
				header: 'Name',
				width: 130,
				dataIndex: 'Name'
			}, {
				header: 'Favorite Color',
				width: 100,
				dataIndex: 'FavoriteColor'
			} ],
			resourceStore: new Sch.data.ResourceStore({
				sortInfo: {
					field: 'Name',
					direction: "ASC"
				},
				model: 'App.CustomResource',
				autoLoad: false,
				proxy: {
					type: 'memory',
					reader: {
						type: 'json'
					}
				}
			}),
			eventStore: new Sch.data.EventStore({
				model: 'Sch.model.Event',
				autoLoad: false,
				proxy: {
					type: 'memory',
					reader: {
						type: 'json'
					}
				}
			})
		});

		// Uncomment this to see what's happening in the EventStore
		// Ext.util.Observable.capture(scheduler.eventStore, function() { console.log(arguments); });

		scheduler.on('eventcontextmenu', this.onEventContextMenu, this);
	},
	onEventContextMenu: function(g, rec, e) {
		e.stopEvent();

		if (!g.gCtx) {
			g.gCtx = new Ext.menu.Menu({
				items: [ {
					text: 'Delete event',
					iconCls: 'icon-delete',
					handler: function() {
						g.eventStore.remove(g.gCtx.rec);
					}
				} ]
			});
		}
		g.gCtx.rec = rec;
		g.gCtx.showAt(e.getXY());
	}
};
