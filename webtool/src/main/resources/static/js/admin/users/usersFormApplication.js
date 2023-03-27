const app = new Vue({
    el: '#content',
    data: {
        dataTableActionLogs: null,
        dataTableSessionLogs: null,
        additionalInfo: undefined,
        recordId: undefined,
        apiError: undefined,
        userId: null
    },
    computed: {},
    methods: {
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
                    targets: 0,
                    width: "220px",
                },
                {
                    targets: 1,
                    width: "200px"
                },
                {
                    targets: 2,
                    width: "360px",
                    render: function (data) {
                        return data ? data : "";
                    }
                },
                {
                    targets: 3,
                    width: "200px",
                    render: function (data) {
                        return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ];

            this.dataTableActionLogs = initDatable('#user-action-logs-table', '/web/admin/users/audit/action/' + ctx.userId, columnsActionLog, orderActionLog, columnDefsActionLog, false, 'filters-action');

            let columnsSessionLog = [
                {'data': 'action'},
                {'data': 'sessionId'},
                {'data': 'createdDate'}
            ];

            let orderSessionLog = [[2, 'desc']];
            let columnDefsSessionLog = [
                {
                    targets: 0, width: "220px"
                },
                {
                    targets: 2, render: function (data) {
                        return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                    }
                }
            ];

            this.dataTableSessionLogs = initDatable('#user-session-logs-table', '/web/admin/users/audit/session/' + ctx.userId, columnsSessionLog, orderSessionLog, columnDefsSessionLog, false, 'filters-session');

        },
        deleteDialog: function (id, name, type) {
            this.additionalInfo = "<br/><b>Username:</b> " + name + "<br/><b>Email:</b> " + type;
            this.recordId = id;
            $('#deleteModal').modal('show');
        },
        confirmDelete: function (id) {
            $('<input />').attr('type', 'hidden').attr('name', 'delete').appendTo('#user-edit-form');
            this.$refs.form.submit();
            $('#deleteModal').modal('hide');
        },
        cancelDialog: function () {
            this.additionalInfo = undefined;
            this.recordId = undefined;
            $('#deleteModal').modal('hide');
        },
        enableUser: function (e) {
            if (e.target.checked) {
                $('#enabled-label').text('Activated');
            } else {
                $('#enabled-label').text('Deactivated');
            }
        }
    },
    beforeCreate() {
    },
    created() {
    },
    beforeMount() {
    },
    mounted() {
        let ctx = this;
        ctx.userId = $('#id').val();
        $('#delete-submit').on('click', function () {
            ctx.deleteDialog($('#id').val(), $('#username').val(), $('#hemail').val());
        });
    },
    beforeUpdate() {
    },
    updated() {
    }
});