const app = new Vue({
    el: '#server-admin',
    data: {
        client: {
            editMode: false,
            timeSettings: {
                ntp: {
                    editMode: false,
                    active: false,
                    servers: []
                },
                timeZone: {
                    editMode: false,
                    selectedTimeZoneId: '',
                    selectedTimeZone: undefined
                }
            },
            network: {
                editMode: false,
                DEFAULT_DNS: '8.8.8.8',
                name: undefined,
                ipAddress: undefined,
                subnetMask: undefined,
                gateway: undefined,
                manualDns: false,
                dns: undefined
		    },
		    mailSettings: {
		        host: undefined,
		        port: undefined,
                userName: undefined,
                password: undefined
		    },
		},
		time: {
		    synchronizationStatus: {
		        ntpActive: false,
		        clockSynchronized: false
		    },
		    syncStatusPolling: undefined,
		    syncStatusInterval: 5000
		},
		networkAdapters: [],
		serverTime: {timezone: ''},
		csrf: '',
		polling: undefined,
		uploadResults: [],
		uploadActive: false,
		apiError: undefined,
		lastTimedErrorId: 0,
		timedErrorLifetime: 10000,
		timedErrors: [],
		lastTimedInfoId: 0,
		timedInfoMessages: []
	},
    computed: {
        timezoneOptions: function() {
            return availableTimezones;
        },
        currentTimeZoneId: function() {
            if(this.serverTime.timezone) {
                return this.serverTime.timezone.id;
            }
            return '';
        },
	    selectedFileLabel: function() {
	        let importFile = this.client.certificate.importFile;
	        let fileSizeLabel = importFile ? humanFileSize(importFile.size) : '';
	        return importFile ? `selected file for upload: "${importFile.name}" (size: ${fileSizeLabel})`
	            : 'please select file for upload';
	    },
        formValid: function() {
            return true;
        },
        uploadButtonDisabled: function() {
            return this.uploadActive || this.client.certificate.importFile === undefined;
        },
        ntpActive: {
            get: function () {
                return this.client.timeSettings.ntp.active;
            },
            set: function (newValue) {
                this.toggleNtpActive(newValue);
                this.initializeNtpServers(newValue, false);
            }
        },
        ntpCheckLabel: function() {
            return this.client.timeSettings.ntp.active ? 'On' : 'Off';
        },
        ntpServersEmpty: function() {
            return this.client.timeSettings.ntp.servers === undefined || this.client.timeSettings.ntp.servers.length == 0;
        },
        syncStatusLabel: function() {
            if(this.time.synchronizationStatus.ntpActive && this.time.synchronizationStatus.clockSynchronized) {
                return 'Clock is synchronized';
            }

            if(this.ntpServersEmpty) {
                return 'Clock not synchronized. Please configure NTP servers';
            }

            return 'Clock not synchronized.\nIf it does not get synchronized for a longer period of time - please contact your administrator';
        },
        syncStatusClass: function() {
            if(this.time.synchronizationStatus.ntpActive && this.time.synchronizationStatus.clockSynchronized) {
                return 'green';
            }

            return 'red';
        }
    },
	watch: {
        'client.timeSettings.ntp.active': function (newNtpActive) {
            if(newNtpActive) {
                this.fetchTimeSyncStatus();
                this.startTimeSyncStatusPolling();
            } else {
                this.stopTimeSyncStatusPolling();
            }
        }
	},
    methods: {
        cancelEditNetworkAdapter: function () {
            this.client.network.editMode = false;
            this.clearNetworkAdapterDto();
        },
        confirmSaveNetworkAdapter: function () {
            $('#confirmModal').modal('show');
        },
        toggleEditAdapterSettings: function (adapterDto) {
            this.client.network.editMode = true;
            this.client.network.name = adapterDto.name;
            this.client.network.ipAddress = adapterDto.ipAddress;
            this.client.network.subnetMask = adapterDto.subnetMask;
            this.client.network.gateway = adapterDto.defaultGateway;
            this.client.network.dns = adapterDto.dns;
            if(adapterDto.dns) {
                this.client.network.manualDns = true;
            } else {
                this.client.network.manualDns = false;
                this.client.network.dns = this.client.network.DEFAULT_DNS;
            }
        },
        confirmAction: function () {
            this.saveNetworkAdapterSettings();
            $('#confirmModal').modal('hide');
        },
        cancelDialog: function () {
            $('#confirmModal').modal('hide');
            this.clearNetworkAdapterDto();
        },
        clearNetworkAdapterDto: function () {
            this.client.network.name = undefined;
            this.client.network.ipAddress = undefined;
            this.client.network.subnetMask = undefined;
            this.client.network.gateway = undefined;
            this.client.network.manualDns = false;
            this.client.network.dns = undefined;
        },
        saveNetworkAdapterSettings: function () {
            let processedDns = this.client.network.manualDns ? this.client.network.dns : null;
            let dtoForSave = {
                name: this.client.network.name,
                ipAddress: this.client.network.ipAddress,
                subnetMask: this.client.network.subnetMask,
                defaultGateway: this.client.network.gateway,
                dns: processedDns
            }
            var vm = this;
            axios
                .post('/web/api/admin/server/network', dtoForSave)
                .then(function (response) {
                    vm.cancelEditNetworkAdapter();
                    vm.apiError = undefined;
                    vm.addTimedInfo("Update Network adapter details includes server restart therefore connection will be interrupted");
                })
                .catch(function (error) {
                    if(error.response && error.response.status === 400) {
                        vm.apiError = error.response.data.message;
                    } else {
                        vm.addTimedError("Server Error occurred while attempting to save configuration of Network adapters. Please contact Your administrator to verify if System is correctly setup");
                    }
                });
        },
        pollTime: function () {
            const vm = this;
            this.polling = setInterval(function() {vm.fetchServerTime()}, 5000);
        },
        fetchServerTime: function () {
            const vm = this;
            axios
                .get('/web/api/admin/server/time/details')
                .then(function (response) {
                    vm.serverTime = response.data;
                })
                .catch(function (error) {
                    vm.apiError = error.message;
                });
        },
        loadMailServerSettings: function () {
            const vm = this;
            axios
                .get('/web/api/admin/server/mail/smtp/settings')
                .then(function (response) {
                    vm.client.mailSettings = response.data;
                })
                .catch(function (error) {
                });
        },
        saveMailServerSettings: function () {
            const vm = this;
            if($("#mail-settings-form").valid()) {
                axios
                    .post('/web/api/admin/server/mail/smtp/settings', vm.client.mailSettings)
                    .then(function (response) {
                        vm.apiError = undefined;
                        vm.addTimedInfo("SMTP server settings updated successfully.");
                    })
                    .catch(function (error) {
                        if(error.response && error.response.status === 400) {
                            vm.apiError = error.response.data.message;
                        } else {
                            vm.addTimedError("Server Error occurred while attempting to save configuration of SMTP servers.");
                        }
                    });
            }
        },
        loadNtpActive: function() {
            const vm = this;
            axios
                .get('/web/api/admin/server/time/synchronization/status')
                .then(function (response) {
                    vm.client.timeSettings.ntp.active = response.data.ntpActive;
                    vm.initializeNtpServers(vm.client.timeSettings.ntp.active, false);
                })
                .catch(function (error) {
                    vm.client.timeSettings.ntp.active = false;
                });
        },
        fetchTimeSyncStatus: function() {
            const vm = this;
            axios
                .get('/web/api/admin/server/time/synchronization/status')
                .then(function (response) {
                    vm.time.synchronizationStatus = response.data;
                })
        },
        toggleNtpActive: function(ntpActiveValue) {
            const formData = new FormData();
            formData.append('ntpActive', ntpActiveValue);
            const headers = {};
            const vm = this;
            axios
                .post('/web/api/admin/server/time/ntp/active', formData, { headers })
                .then(function (response) {
                    vm.client.timeSettings.ntp.active = ntpActiveValue;
                })
                .catch(function (error) {
                    vm.addTimedError("Server Error occurred while attempting to store NTP active setting. Please contact Your administrator to verify if System is correctly setup");
                });
	    },
	    initializeNtpServers: function(newNtpValue, initSingleEmptyRecord) {
            if(newNtpValue && this.ntpServersEmpty) {
                this.loadNtpServers(initSingleEmptyRecord);
            }
	    },
        startTimeSyncStatusPolling: function () {
            if(this.time.syncStatusPolling === undefined) {
                const vm = this;
                this.time.syncStatusPolling = setInterval(function() { vm.fetchTimeSyncStatus(); }, this.time.syncStatusInterval);
            }
        },
        stopTimeSyncStatusPolling: function () {
            if (this.time.syncStatusPolling) {
                clearInterval(this.time.syncStatusPolling);
                this.time.syncStatusPolling = undefined;
            }
        },
        toggleEditNtpServers: function () {
            this.client.timeSettings.ntp.editMode = true;
            this.initializeNtpServers(true, true);
        },
        addNtpServer: function () {
            let ntpServerDto = {
                type: 'SERVER',
                address: '',
                options: ''};
            this.client.timeSettings.ntp.servers.push(ntpServerDto);
        },
        saveNtpServers: function () {
            this.client.timeSettings.ntp.editMode = false;
            let dtoForSave = {
                serverConfigurations: this.client.timeSettings.ntp.servers
            }
            var vm = this;
            axios
                .post('/web/api/admin/server/time/ntp/servers', dtoForSave)
                .then(function (response) {
                    vm.addTimedInfo("Storing NTP servers configuration requires restarting Synchronization service. It may take few moments for changes to take effect.");
                    if(vm.ntpServersEmpty) {
                         vm.ntpActive = false;
                    }
                })
                .catch(function (error) {
                    vm.addTimedError("Server Error occurred while attempting to save configuration of NTP servers. Please contact Your administrator to verify if System is correctly setup");
                });
        },
        removeNtpServer: function(serverIndex) {
            this.client.timeSettings.ntp.servers.splice(serverIndex, 1);
        },
        cancelEditNtpServers: function () {
            this.client.timeSettings.ntp.editMode = false;
            this.loadNtpServers(false);
        },
        loadNtpServers: function(initSingleEmptyRecord) {
            const vm = this;
            axios
                .get('/web/api/admin/server/time/ntp/servers')
                .then(function (response) {
                    vm.client.timeSettings.ntp.servers = response.data.serverConfigurations;

                    let ntpServerDataEmpty = response.data.serverConfigurations === undefined || response.data.serverConfigurations.length == 0;
                    if(initSingleEmptyRecord && ntpServerDataEmpty) {
                        vm.client.timeSettings.ntp.servers = [{
                            address: '',
                            type: 'SERVER',
                            options: ''
                        }];
                    }
                })
                .catch(function (error) {
                    vm.addTimedError("Server Error occurred while attempting to load settings of NTP servers. Please contact Your administrator to verify if System is correctly setup");
                });
        },
        toggleEditTimeZone: function () {
            this.client.timeSettings.timeZone.editMode = true;
            let currentTimeZone = this.timezoneOptions
                .find(timeZone => {
                    return timeZone.id === this.currentTimeZoneId;
                });
            if(currentTimeZone) {
                this.client.timeSettings.timeZone.selectedTimeZone = currentTimeZone;
                this.client.timeSettings.timeZone.selectedTimeZoneId = currentTimeZone.id;
            }
        },
        saveTimeZone: function () {
            this.client.timeSettings.timeZone.editMode = false;
            const formData = new FormData();
            formData.append('timeZone', this.client.timeSettings.timeZone.selectedTimeZone.id);
            const headers = {};
            const vm = this;
            axios
                .post('/web/api/admin/server/time/zone', formData, { headers })
                .then(function (response) {
                })
                .catch(function (error) {
                    vm.addTimedError("Server Error occurred while attempting to save selected Time Zone. Please contact Your administrator to verify if System is correctly setup");
                });
        },
        cancelEditTimeZone: function () {
            this.client.timeSettings.timeZone.editMode = false;
        },
        toggleEdit: function () {
            this.client.editMode = true;
        },
        updateManualDateTime: function(startDate) {
            const formData = new FormData();
            formData.append('dateTime', startDate);
            const headers = {};
            const vm = this;
            axios
                .post('/web/api/admin/server/time', formData, { headers })
                .then(function (response) {
                })
                .catch(function (error) {
                    vm.addTimedError("Server Error occurred while attempting to set Time. Please contact Your administrator to verify if System is correctly setup");
                });
        },
	    getTimedErrorId: function() {
	        this.lastTimedErrorId++;
	        return this.lastTimedErrorId;
	    },
        addTimedError: function(errorMessage) {
            let errorId = this.getTimedErrorId();
            let timedError = {
                id: errorId,
                message: errorMessage
            }
            this.timedErrors.push(timedError);
            var vm = this;
            setTimeout(() => {
                vm.removedTimedError(errorId)
            }, this.timedErrorLifetime)
        },
        removedTimedError: function (messageId) {
            const errorToRemoveIndex = this.timedErrors
                .findIndex(function(timedItem) {
                    return timedItem.id === messageId;
                });
            if (errorToRemoveIndex !== -1) {
                this.timedErrors.splice(errorToRemoveIndex, 1);
            }
        },
	    getTimedInfoId: function() {
	        this.lastTimedInfoId++;
	        return this.lastTimedInfoId;
	    },
        addTimedInfo: function(infoMessage) {
            let infoId = this.getTimedInfoId();
            let timedInfo = {
                id: infoId,
                message: infoMessage
            }
            this.timedInfoMessages.push(timedInfo);
            const vm = this;
            setTimeout(() => {
                vm.removedTimedInfo(infoId)
            }, this.timedErrorLifetime)
        },
        removedTimedInfo: function (messageId) {
            const infoToRemoveIndex = this.timedInfoMessages
                .findIndex(function(timedItem) {
                    return timedItem.id === messageId;
                });
            if (infoToRemoveIndex !== -1) {
                this.timedInfoMessages.splice(infoToRemoveIndex, 1);
            }
        },
        notImplementedServerAction: function() {
            this.addTimedError('Save action for Server settings is not implemented yet');
        }
	},
	mounted() {
        this.csrf = this.$refs.csrf.value;
        axios.defaults.headers.common['X-CSRF-TOKEN'] = this.csrf;

        this.$root.$on('updateManualDateTime', (startDate) => {
            this.updateManualDateTime(startDate);
        })

        this.$root.$on('emitChangeAdapter', (adapterDto) => {
            this.toggleEditAdapterSettings(adapterDto);
        })

        this.$root.$on('emitDeleteNtpServer', (serverIndex) => {
            this.removeNtpServer(serverIndex);
        })
    },
    created() {
        this.fetchServerTime();
        this.loadNtpActive();
        this.loadMailServerSettings();
        this.pollTime();
    },
    beforeDestroy () {
        if (this.polling) {
            clearInterval(this.polling);
            this.polling = undefined;
        }
    }
});
