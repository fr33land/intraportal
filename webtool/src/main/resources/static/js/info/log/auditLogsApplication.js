new Vue({
    el: '#content',
    data: {
        dataTables: [],
        recordId: undefined
    },
    methods: {
        clearSearchInput: function (el, dataTableId) {
            $(`${dataTableId} input[type=search]`).val('');
            el.addClass("disabled");
            this.dataTables[dataTableId].search('').draw();
        },
        updateButtonState: function (el, dataTableId) {
            if (el.val().length > 0) {
                $(`${dataTableId}_filter a`).removeClass("disabled");
            } else {
                $(`${dataTableId}_filter a`).addClass("disabled");
            }
        },
    },
    mounted() {
        let ctx = this;
        ctx.userId = $('#id').val();

        let columnsActionLog = [
            {'data': 'actor'},
            {'data': 'domain'},
            {'data': 'action'},
            {'data': 'sessionId'},
            {'data': 'createdDate'}
        ];

        let orderActionLog = [[4, 'desc']];
        let columnDefsActionLog = [
            {
                targets: 0,
                width: "170px"
            },
            {
                targets: 1,
                width: "170px"
            },
            {
                targets: 2,
                width: "170px"
            },
            {
                targets: 3,
                width: "450px",
                render: function (data) {
                    return data ? data : "";
                }
            },
            {
                targets: 4,
                width: "250",
                render: function (data) {
                    return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                }
            }
        ];

        initDatable('#action-log-table', '/web/audit/log/action/list', columnsActionLog, orderActionLog, columnDefsActionLog, true, 'filters-action');

        let columnsSessionLog = [
            {'data': 'username'},
            {'data': 'action'},
            {'data': 'sessionId'},
            {'data': 'createdDate'}
        ];

        let orderSessionLog = [[3, 'desc']];
        let columnDefsSessionLog = [
            {
                targets: 0, width: "270px"
            },
            {
                targets: 1, width: "270px"
            },
            {
                targets: 2, width: "450px",
                render: function (data) {
                    return data ? data : "";
                }
            },
            {
                targets: 3, width: "250px",
                render: function (data) {
                    return data ? moment(data).format('YYYY-MM-DD HH:mm:ss') : "";
                }
            }
        ];

        initDatable('#session-log-table', '/web/audit/log/session/list', columnsSessionLog, orderSessionLog, columnDefsSessionLog, true, 'filters-session');

    }
});