<html>
	<head><title>Main</title></head>
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
	
	
	
	
	<body>
	<div id="wrapper">	
		<div id="header">
		<h1> ProjectFunder </h1>
		</div>
		
		<br><br>
		
		<a href="./view_profile?ersteller=dummy@dummy.com">
			<button type="button" style="background-color:blue;border-color:black;color:white;height:60;width:32.5%">
				Mein Profil
			</button>
		</a>
		
		<br><br>
		<div id="header">
			<h1> Offene Projekte </h1>
		</div>


	    <#list offene_Projekte as o>
	    
		<br><br>
	  
	    <img src="${o.icon}" alt="icon" >
	    <a href="./view_project?kennung=${o.kennung}">
	    	<h2>${o.titel}</h2>
	    </a>
	    
	    <h2>von:
	    	<a href="./view_profile?ersteller=${o.email}">
				${o.ersteller}</a>
		</h2>

		<h2>Aktuell: ${o.spendensumme} &euro; </h2>
		
		<br><br>
		<hr>

	
		
    	</#list>

		
		<br><br>
		<div id="header">
			<h1> Abgeschlossene Projekte </h1>
		</div>
		<br><br>

		
		<#list geschlossene_Projekte as g>
	    <br><br>

	    
	    <img src="${g.icon}" alt="icon">
	    <a href="./view_project?kennung=${g.kennung}">
	    	<h2>${g.titel}</h2>
	    </a>
	    
	    <h2>von: <a href="./view_profile?ersteller=${g.ersteller}" >
				${g.ersteller} </a>
		</h2>


		<h2>Aktuell: ${g.spendensumme} &euro; </h2>
		
		<br><br>
		<hr>
		
		
    	</#list>
    	
		<br><br>
		<div align="right">
			<a style="text-decoration:none" href="./new_project">
				<button type="button" style="background-color:blue; border-color:black; color:white;height:60;width:32.5%">
					Projekt erstellen
				</button>
			</a>
		</div>
	</div>
	</body>
</html>