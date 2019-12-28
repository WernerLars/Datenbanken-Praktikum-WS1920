<html>
	<head><title>View Profile</title></head>
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
		<h1> Profil von ${email} </h1>
	</div>
	<br><br>
	
	<h2>Benutzername: ${name} </h2>
	<h2>Anzahl erstellter Projekte: ${anzerprojekte} </h2>
	<h2>Anzahl unterstützte Projekte: ${anzunprojekte} </h2>
	
	<br><br>
	
	<div id="header">
		<h1> Erstellte Projekte </h1>
	</div>
	
	<br><br>
	
	<#list erstpro as e>
		
		<img src="${e.icon}" alt="icon" >
		
		<h2><a href="./view_project?kennung=${e.kennung}" >
			 ${e.titel}</a>
		</h2>
		<h2>Aktuell: ${e.spendensumme}</h2>
		<h2>Status: ${e.status}</h2>
		
		<br><br>
		<hr>
		<br><br>
		
	</#list>
	

	
	<br><br>
	
	<div id="header">
		<h1> Unterstützte Projekte </h1>
	</div>
	
	<br><br>
	
		<#list unpro as o>
		
		<img src="${o.icon}" alt="icon" >
		
		<h2><a href="./view_project?kennung=${o.kennung}" >
			 ${o.titel}</a>
		</h2>
		<h2>Limit: ${o.finanzierungslimit}</h2>
		<h2>Status: ${o.status}</h2>
		<h2>Gespendet: ${o.spendenbetrag}</h2>
		
		<br><br>
		<hr>
		<br><br>
		
	</#list>







</div>
</html>
