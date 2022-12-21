/* 모달 오픈 공통함수 S */
var openModal = {
    /* Alert */
    alert: function(msg, okhandler) {
        new Promise((resolve, reject) => {
            $("#msg_popup #btn_confirm").hide();
            $("#msg_popup #btn_alert").show();

            $("#msg_popup #alert_ok").unbind();
            $("#msg_popup .modal-body").html(msg);
            $('#msg_popup').modal('show');

            $("#msg_popup #alert_ok").click(function() {
                $('#msg_popup').modal('hide');
            });

            $("#msg_popup").on("hidden.bs.modal", function(e) {
                e.stopPropagation();
                if(okhandler != null) resolve();
                else reject();
            });
        }).then(okhandler).catch(function() {});
    },

    /* Confirm */
    confirm: function(msg, yeshandler, nohandler) {
        new Promise((resolve, reject) => {
            var flag = false;
            $("#msg_popup #btn_alert").hide();
            $("#msg_popup #btn_confirm").show();

            $("#msg_popup #confirm_yes").unbind();
            $("#msg_popup #confirm_no").unbind();
            $("#msg_popup .modal-body").html(msg);
            $('#msg_popup').modal('show');

            $('#msg_popup').on('keypress', function (e) {
                var keycode = (e.keyCode ? e.keyCode : e.which);
                if(keycode == '13') {
                    flag = true;
                    $('#msg_popup').modal('hide');
                }
            });

            $("#msg_popup #confirm_yes").click(function() {
                flag = true;
            });
            $("#msg_popup #confirm_no").click(function() {
                flag = false;
            });

            $("#msg_popup").on("hidden.bs.modal", function(e) {
                e.stopPropagation();
                if(yeshandler != null && flag == true) resolve(1);
                else if(nohandler != null && flag == false) resolve(2);
                else reject();
            });

        }).then(function(value) {
            if(value == 1)      yeshandler();
            else if(value == 2) nohandler();
        }).catch(function() {});
    },
}
/* 모달 오픈 공통함수 E */

/* 공통 함수 S */
var commonJS = {

    // 빈 값 체크
    isEmpty : function(value){
        if( value == '' || value == null || value == undefined || ( value != null && typeof value == 'object' && !Object.keys(value).length ) ){
            return true;
        }else{
            return false;
        }
    },

    // 태그 문자 있는지 체크
    isRegExp : function(value) {
        console.log('value ===> ', value);
        if(!this.isEmpty(value)) {
            var regexp = /(<([^>]+)>)/ig;
            if(value.match(regexp)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    },

    // 태그 제거
    regExp : function(value) {
        var regexp = /(<([^>]+)>)/ig;
        var returnVal = '';
        returnVal = value.replace(regexp, '');
        return returnVal;
    },

    // 태그 변환
    convertTag : function(str) {
        str = str.replace(/</g, '&lt;');
        str = str.replace(/>/g, '&gt;');
        str = str.replace(/\"/g, '&quot;');
        str = str.replace(/\'/g, '&#39;');
        str = str.replace(/\n/g, '<br />');
        return str;
    },

    // 태그 변환
    convertTagRevert : function(str) {
        str = str.replace('&lt;', '<');
        str = str.replace('&gt;', '>');
        str = str.replace('&quot;', '"');
        str = str.replace('&#39;', "'");
        str = str.replace('<br />', '\n');
        return str;
    }

}
/* 공통 함수 E */