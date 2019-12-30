<html>
	<head><title>View Project</title></head>
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
		<h1> Informationen </h1>
	</div>
	<br><br>
	<div align="center">
	<img src="${icon}" alt="icon" >

	<h2>${titel}</h2>

	<h3>von: <a href="./view_profile?ersteller=${email}" >
			 ${ersteller}</a>
	</h3>
	
	<h3>${beschreibung}</h3>

	</div>
	<br><br>
	
	<h2>Finanzierungslimit: ${finanzierungslimit} &euro;</h2>
	<h2>Aktuelle Spendensumme: ${spendensumme} &euro;</h2>
	<h2>Status: ${status}</h2>

	<h2>Vorgänger-Projekt: ${code} </h2>

	
	<br><br>
	<div id="header">
		<h1> Aktionsleiste </h1>
	</div>
	<br><br>
		
		<p style="color:red;" align="center">${fehler}</p>
		<br><br>
 		 <a style="text-decoration:none" href="./new_project_fund?kennung=${kennung}">
			<button type="button" style="background-color:green; border-color:black; color:white;height:60;width:32.5%">
				Spenden
			</button>
		</a>
		
 	 	<a style="text-decoration:none" href="./delete_project?kennung=${kennung}">
			<button type="button" style="background-color:red; border-color:black; color:white;height:60;width:32.5%">
				Projekt Löschen
			</button>
		</a>
		
 		 <a style="text-decoration:none" href="./edit_project?kennung=${kennung}">
			<button type="button" style="background-color:blue; border-color:black; color:white;height:60;width:32.5%">
				Projekt editieren
			</button>
		</a>
 
	
	
	<br><br>
	<div id="header">
		<h1> Liste der Spender </h1>
	</div>
	
	<br><br>
	
	<#list spender as s>
		<h2>${s.name}: ${s.spendenbetrag} &euro; </h2>
	
	</#list>
	
	<br><br>
	<div id="header">
		<h1> Kommentare </h1>
	</div>
	
	<br><br>
	
	<#list kommentare as k>
		<h2>${k.name}: ${k.text}  </h2>
	
	</#list>



	<br><br>
	<div align="right">
			<a style="text-decoration:none" href="./new_comment?kennung=${kennung}">
				<button type="button" style="background-color:blue; border-color:black; color:white;height:60;width:32.5%">
					Kommentieren
				</button>
			</a>
		</div>


</div>

</html>