$(document).ready(function () {
    $("#login-form").validate({
        onkeyup: false,
        errorElement: "span",
        errorClass: "is-invalid",
        rules: {
            username: "required",
            password: "required"
        },
        messages: {
            username: "Please enter username",
            password: "Please enter password"
        }
    });
});