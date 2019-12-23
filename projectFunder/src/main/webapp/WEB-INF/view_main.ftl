<html>
	<head><title>Main</title></head>
	<body>
	
		<h1 align="center"> ProjectFunder </h1>

		<a href="./view_profile" target="_blank">
			<button type="button" style="background-color:blue;border-color:black;color:white">
				Mein Profil
			</button>
		</a>

		<h2> Offene Projekte </h2>


	    <#list offene_Projekte as o>
	    
	    <h3>${o.titel}</h3>
		<p>von: ${o.ersteller}</p>
		<p>Aktuell: ${o.spendensumme} &euro; </p>

    	</#list>


		<h2> Abgeschlossene Projekte </h2>

		<#list geschlossene_Projekte as g>
	    
	    <h3>${g.titel}</h3>
		<p>von: ${g.ersteller}</p>
		<p>Aktuell: ${g.spendensumme} &euro; </p>
		
    	</#list>

		<div align="right">
			<a href="./new_project" target="_blank">
				<button type="button" style="background-color:blue; border-color:black; color:white">
					Projekt erstellen
				</button>
			</a>
		</div>

	</body>
</html>