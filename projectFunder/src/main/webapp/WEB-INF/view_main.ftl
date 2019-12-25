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
		<a href="./view_profile?ersteller=dummy@dummy.de" target="_blank">
			<button type="button" style="background-color:blue;border-color:black;color:white;height:30px;width:100px">
				Mein Profil
			</button>
		</a>

		<h2> Offene Projekte </h2>

	    <#list offene_Projekte as o>
	    
	    <hr>
	  
	    <img src="${o.icon}" alt="icon" >
	    <a href="./view_project?kennung=${o.kennung}" target="_blank">
	    	<h3>${o.titel}</h3>
	    </a>
	    <a href="./view_profile?ersteller=${o.ersteller}" target="_blank">
			<p>von: ${o.ersteller}</p>
		</a>
		<p>Aktuell: ${o.spendensumme} &euro; </p>

		<hr>
		
    	</#list>


		<h2> Abgeschlossene Projekte </h2>

		<#list geschlossene_Projekte as g>
	    
	    <hr>
	    
	    <img src="${g.icon}" alt="icon">
	    <a href="./view_project?kennung=${g.kennung}" target="_blank">
	    	<h3>${g.titel}</h3>
	    </a>
	    <a href="./view_profile?ersteller=${g.ersteller}" target="_blank">
			<p>von: ${g.ersteller}</p>
		</a>
		<p>Aktuell: ${g.spendensumme} &euro; </p>
		
		<hr>
		
    	</#list>

		<div align="right">
			<a href="./new_project" target="_blank">
				<button type="button" style="background-color:blue; border-color:black; color:white;height:30px;width:100px">
					Projekt erstellen
				</button>
			</a>
		</div>
	</div>
	</body>
</html>