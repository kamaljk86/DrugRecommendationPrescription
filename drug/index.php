<!DOCTYPE html>
<html>
  <head>
    <meta charset=utf-8 />
    <title>DRUG</title>
    <link href="css/style.css" rel="stylesheet">
    <script src="js/jquery.min.js"></script>
    <script src="js/custom.js"></script> 
  </head>
  <body>
    <p>&nbsp;</p>  
    <div id='login_cnt'>
      <div class="login_form">         
        <div id="login_page">
          <form>
            <div class="container">
              <p><input type="text" placeholder="Enter Drugname" name="dname" id="dname" ></p>
              <p><input type="text" placeholder="Enter Manufactured by" name="mname" id="mname" ></p>
              <p><input type="text" placeholder="Enter Used for" name="uname" id="uname" ></p>
              <p><input type="text" placeholder="Enter Rate" name="rname" id="rname" ></p>
              <p><textarea class="post_skils" name="post_skils" rows="4" cols="50" id='post_skils' placeholder="Enter Description..."></textarea></p>
              <div id="loader11" style="display: none;text-align:center;">
                <img src="img/loader.gif" style="width:100px;">
              </div>
              <p style="text-align:left;"><input type="button" id='login_btn' value='Login' /></p>
            </div>
          </form>
        </div> 
      </div>
    </div>
  </body>
</html>  
