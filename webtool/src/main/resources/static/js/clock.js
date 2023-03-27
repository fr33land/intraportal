new Vue({
    el: '#system-clock',
    data: {
        clock: {
            refreshInterval: 1000,
            syncWithSystemCount: 10,
            syncedLocalDateTime: undefined,
            localToSystemDiffDuration: undefined,
            utcTime: false,
            tickCount: 0
        },
        loader: {
            url: '/web/info/status/time',
            polling: undefined
        }
    },
    computed: {
        systemDateTime: {
            get: function() {
                if(this.clock.syncedLocalDateTime) {
                    return this.clock.syncedLocalDateTime;
                }

                return undefined;
            },
            set: function(newLocalDateTimeMoment) {
                if(this.clock.localToSystemDiffDuration) {
                    this.clock.syncedLocalDateTime = newLocalDateTimeMoment.add(this.clock.localToSystemDiffDuration);
                }
            }
        },
        timeFormat: function () {
            return this.clock.utcTime ? 'HH:mm:ss[Z]' : 'HH:mm:ss';
        }
    },
    filters: {
        formatDate: function (value) {
            if (!value) return '';
            return value.format('YYYY-MM-DD');
        },
        formatTime: function (value, timeFormat) {
            if (!value) return '';
            return value.format(timeFormat);
        }
    },
	methods: {
		startClock: function(updateInterval) {
		    var vm = this;
            this.loader.polling = setInterval(function() { vm.updateClock(); }, updateInterval);
        },
        stopClock: function() {
            if (this.loader.polling) {
                clearInterval(this.loader.polling);
                this.loader.polling = undefined;
            }
        },
        updateClock: function() {
            this.clock.tickCount++;
            let syncWithSystem = this.clock.tickCount % this.clock.syncWithSystemCount === 0;
            this.systemDateTime = moment();
            if(syncWithSystem) {
                this.fetchClockAndSync();
            }
        },
        syncUIClock: function(latestTimeUpdate) {
            let latestSystemDateTime = moment(latestTimeUpdate);
            let systemTimeDiffToLocal = latestSystemDateTime.diff(moment());
            this.clock.localToSystemDiffDuration = moment.duration(systemTimeDiffToLocal);
        },
        initializeClock: function() {
            var vm = this;
            axios
                .get(vm.loader.url)
                .then(function (response) {
                    if(response.request.responseURL.endsWith(vm.loader.url)) {
                        vm.syncUIClock(response.data.dateTime);
                        vm.clock.utcTime = response.data.utc;
                        vm.updateClock();
                        vm.startClock(vm.clock.refreshInterval);
                    }
                })
                .catch(function (error) {
                });
        },
        fetchClockAndSync: function() {
            var vm = this;
            axios
                .get(vm.loader.url)
                .then(function (response) {
                    if(response.request.responseURL.endsWith(vm.loader.url)) {
                        vm.syncUIClock(response.data.dateTime);
                        vm.clock.utcTime = response.data.utc;
                    }
                })
                .catch(function (error) {
                });
        }
    },
    beforeDestroy() {
        this.stopClock();
    },
    created() {
        this.initializeClock();
    }
});
