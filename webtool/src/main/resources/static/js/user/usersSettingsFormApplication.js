const app = new Vue({
    el: '#content',
    data: {
        dataTableActionLogs: null,
        dataTableSessionLogs: null,
        recordId: undefined,
        apiError: undefined,
        userId: null
    },
    computed: {},
    methods: {
        updateButtonState: function (el) {
            if (el.val().length > 0) {
                $(".clear-filter").removeClass("disabled");
            } else {
                $(".clear-filter").addClass("disabled");
            }
        },
        showUserLogs: function () {
            let ctx = this;
            ctx.userId = $('#id').val();

            let columnsActionLog = [
                {'data': 'domain'},
                {'data': 'action'},
                {'data': 'sessionId'},
                {'data': 'createdDate'}
            ];

            let orderActionLog = [[3, 'desc']];
            let columnDefsActionLog = [
                {
                    targets: 0, width: "220px"
                },
                {
                    targets: 1, width: "220px"
                },
                {
                    targets: 2, width: "380px",
                    render: function (data) {
                        return data ? data : "";
                    }
                },
                {
                    targets: 3, width: "200px",
                    render: function (data) {
                        return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ];

            this.dataTableActionLogs = initDatable('#user-action-logs-table', '/web/user/audit/action', columnsActionLog, orderActionLog, columnDefsActionLog, false, 'filters-action');

            let columnsSessionLog = [
                {'data': 'action'},
                {'data': 'sessionId'},
                {'data': 'createdDate'}
            ];

            let orderSessionLog = [[2, 'desc']];
            let columnDefsSessionLog = [
                {
                    targets: 0, width: "120px"
                },
                {
                    targets: 1, width: "380px",
                    render: function (data) {
                        return data ? data : "";
                    }
                },
                {
                    targets: 2, render: function (data) {
                        return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ];

            this.dataTableSessionLogs = initDatable('#user-session-logs-table', '/web/user/audit/session', columnsSessionLog, orderSessionLog, columnDefsSessionLog, false, 'filters-session');

        }
    },
    beforeCreate() {
    },
    created() {
    },
    beforeMount() {
    },
    mounted() {
    },
    beforeUpdate() {
    },
    updated() {
    }
});