Ext.Loader.setConfig({
	enabled: true,
	paths: {
		'chat': 'app'
	}
});

Ext.onReady(function() {

	/*
	Deft.Injector.configure({
		carChart: 'carstore.store.CarChart',
		carData: 'carstore.store.CarData'
	});
	*/

	Ext.create('chat.view.Viewport');
});
