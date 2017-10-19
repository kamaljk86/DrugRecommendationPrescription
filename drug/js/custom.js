$(document).ready(function()
{   
    $('#dname').focus(function() {
        $('#dname').css('border','1px solid #999');
        $("#loader11").hide();
    });
    $('#mname').focus(function() {
        $('#mname').css('border','1px solid #999');
        $("#loader11").hide();
    });
    $('#uname').focus(function() {
        $('#uname').css('border','1px solid #999');
        $("#loader11").hide();
    });
    $('#rname').focus(function() {
        $('#rname').css('border','1px solid #999');
        $("#loader11").hide();
    });
    $('#post_skils').focus(function() {
        $('#post_skils').css('border','1px solid #999');
        $("#loader11").hide();
    });
    $("#login_btn").click(function()
    {
        var dname         = $('#dname').val();
        var mname         = $('#mname').val();
        var uname         = $('#uname').val();
        var rname         = $('#rname').val();
        var post_skils    = $('#post_skils').val();
        if(dname=='')
        {
            $('#dname').css('border','1px solid #ff0000');
        }
        else if(mname=='')
        {
            $('#mname').css('border','1px solid #ff0000');
        }
        else if(uname=='')
        {
            $('#uname').css('border','1px solid #ff0000');
        }
        else if(rname=='')
        {
            $('#rname').css('border','1px solid #ff0000');
        }
        else if(post_skils=='')
        {
            $('#post_skils').css('border','1px solid #ff0000');
        }           
        else
        {
            $('#loader11').show(400);
            var dataString = 'dname=' + dname + '&mname=' + mname  + '&uname=' + uname  + '&rname=' + rname  + '&post_skils=' + post_skils;
            $.ajax({
                type: "POST",
                url: "process.php",     
                data: dataString,
                cache: false,
                success: function(result)
                {                       
                    if(result.search("Successfully") >= 0)
                    {   
                        $("#loader11").hide();                           
                        $("#loader11").fadeIn(400).html(result); 
                        window.location='live.php'; 
                    }
                    else
                    {
                        $("#loader11").hide();
                        $("#loader11").fadeIn(400).html(result); 
                    }
                }
            })
        }
    });    
});  