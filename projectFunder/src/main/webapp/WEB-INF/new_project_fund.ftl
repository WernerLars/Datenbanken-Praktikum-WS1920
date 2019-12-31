<html>
	<head><title>New Project Fund</title></head>
	<style type="text/css">
* {
   margin:0;
   padding:0;
}

body{
   text-align:center;
   background: #efe4bf none repeat scroll 0 0;
}

#wrapper{
   width:960px;
   margin:0 auto;
   text-align:left;
   background-color: #fff;
   border-radius: 0 0 10px 10px;
   padding: 20px;
   box-shadow: 1px -2px 14px rgba(0, 0, 0, 0.4);
}

#header{
 color: #fff;
 background-color: #2c5b9c;
 height: 3.5em;
 padding: 1em 0em 1em 1em;
 
}

#site{
    background-color: #fff;
    padding: 20px 0px 0px 0px;
}
.centerBlock{
	margin:0 auto;
}



</style>

<div id="wrapper">

	<div id="header">
		<h1> ${titel} </h1>
	</div>
	<br><br>
	
	<h3 style="color:red">${fehler}</h3>
	
	<br><br>
	
	<form action="new_project_fund" method="post">
	<h2> Spendenbetrag (&euro;): <input type="text" name="spende" /></h2><br/>
	
	<h2><input type="checkbox" name="anonym"> Anonym spenden? </h2>
	<br><br>
	<input type="submit" value="Spenden" style="background-color:red; border-color:black; color:white;height:60;width:32.5%">
	</form>
	
	
	
</div>

</html>
	
	