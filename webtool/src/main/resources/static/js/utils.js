function trimInputField(fieldValue) {
    return fieldValue === undefined ? '' : fieldValue.trim();
}

function mapToRangesDto(ranges) {
    let mappedRanges = {};

    ranges.forEach(range => {
        mappedRanges[range.label] = [moment(range.startDate), moment(range.endDate)];
    });

    return mappedRanges;
}

/**
 * Format bytes as human-readable text.
 *
 * @param bytes Number of bytes.
 * @param si True to use metric (SI) units, aka powers of 1000. False to use
 *           binary (IEC), aka powers of 1024.
 * @param dp Number of decimal places to display.
 *
 * @return Formatted string.
 */
function humanFileSize(bytes, si = false, dp = 1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }

    const units = si
        ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10 ** dp;

    do {
        bytes /= thresh;
        ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);


    return bytes.toFixed(dp) + ' ' + units[u];
}

function fetchInterval(fetchPolicy, failedRequests) {
    let scaleLevel = 1;
    if (failedRequests > 0) {
        scaleLevel = fetchPolicy.backoffCoefficient ** (failedRequests - 1);
    }
    let calculatedInterval = fetchPolicy.initialInterval * scaleLevel;
    let constrainedInterval = calculatedInterval < fetchPolicy.maximumInterval ? calculatedInterval : fetchPolicy.maximumInterval;
    return constrainedInterval;
}

function getApiFetchErrorText(entityName, fetchPolicy, error) {
    if (error.response) {
        switch (error.response.status) {
            case 401:
            case 403:
                return `Authentication lost. Please refresh page and perform Log in again`;
            case 404:
                return `${entityName} refresh faces issue - Service unreachable`;
            case 500:
                return `${entityName} refresh faces issue on the Server side`;
            default:
                return `Connection Lost? ${entityName} refresh faces issue: ${error.message}`;
        }
    } else {
        return `Connection Lost? ${entityName} refresh faces issue: ${error.message}`;
    }
}

function createApiForbiddenError() {
    let apiError403 = {
        response: {
            status: 403
        }
    };
    return apiError403;
}

function initDatable(e, url, columns, order, columnDefs, ordering, filtersName) {
    if ($.fn.DataTable.isDataTable(e)) {
        return $(e).DataTable();
    }

    let dataTable = $(e).DataTable({
        'dom': '<t><"row justify-content-right"<"col-auto"p>><"clear">',
        'ajax': {
            'contentType': 'application/json',
            'url': url,
            'type': 'POST',
            'data': function (d) {
                return JSON.stringify(d);
            }
        },
        'pageLength': 10,
        'responsive': true,
        'autoWidth': false,
        'ordering': ordering,
        'bLengthChange': false,
        'columns': columns,
        'order': order,
        'columnDefs': columnDefs,
        'language': {
            'search': '_INPUT_',
            'searchPlaceholder': 'Filter'
        },
        'initComplete': function () {
            $('.paginate_button').addClass('btn btn-light');
            let api = this.api();

            api
                .columns()
                .eq(0)
                .each(function (colIdx) {
                    let cell = $('.' + filtersName + ' th').eq(
                        $(api.column(colIdx).header()).index()
                    );

                    $(cell).removeClass("sorting").unbind('click').html('<input style="width: 100%" type="search" placeholder="Filter"/>');

                    $('input', $('.' + filtersName + ' th').eq($(api.column(colIdx).header()).index()))
                        .off('keyup change')
                        .on('change', function (e) {
                            $(this).attr('title', $(this).val());
                            api
                                .column(colIdx)
                                .search(this.value, this.value != '', this.value == '')
                                .draw();
                        })
                        .on('keyup', function (e) {
                            e.stopPropagation();
                            $(this).trigger('change');
                        });
                });

            $('.' + filtersName + ' th:last')
                .css("text-align", "right")
                .css("padding-right", "0px")
                .append("<a href=\"#\" id=\"" + filtersName + "-clear-filter\" class=\"btn btn-light clear-filter\"><i class=\"fa fa-trash-alt fa-fw\"></i></a>");

            $('#' + filtersName + '-clear-filter').on('click', function () {
                $(this).parent().parent().children('th').children('input[type=search]').val('');
                api.columns().search('').draw();
            });
        },
        'drawCallback': function () {
            $('.paginate_button').addClass('btn btn-light');
        },
        'serverSide': true
    });

    $(e + ' thead tr')
        .clone(true)
        .addClass(filtersName + ' search-fields-container')
        .prependTo(e + ' thead');

    let colCnt = $(e + ' thead th').length + 1;
    $(e).find('tr:last').before('<tr style="background: #ffffff"><th style="padding-top: 0px; padding-left: 0px; padding-right: 0px;" colspan="' + colCnt + '"><div class="search-field-container-thin"></div></th></tr>');

    return dataTable;
}