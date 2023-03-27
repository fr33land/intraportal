$(function() {
    function cb(start) {
        app.$root.$emit('updateManualDateTime', start.format('YYYY-MM-DD HH:mm:ss'));
    }

    $(datePickerIdSelector)
        .daterangepicker({
            "singleDatePicker": true,
            "timePicker": true,
            "timePicker24Hour": true,
            "timePickerSeconds": true,
            "startDate": startMoment,
            "drops": "auto"
        }, cb);

});