<html>
	<head>
		<title>Projekt erstellen</title>
		<style type="text/css">
			* {
				margin: 0;
				padding: 0;
			}

			body{
				text-align: center;
				background: #efe4bf none repeat scroll 0 0;
			}

			#wrapper {
				width: 960px;
				margin:0 auto;
				text-align: left;
				background-color: #fff;
				border-radius: 0 0 10px 10px;
				padding: 20px;
				box-shadow: 1px -2px 14px rgba(0, 0, 0, 0.4);
			}

			#header {
				color: #fff;
				background-color: #2c5b9c;
				height: 3.5em;
				padding: 1em 0em 1em 1em;
			}

			#site {
				background-color: #fff;
				padding: 20px 0px 0px 0px;
			}

			.centerBlock{
				margin:0 auto;
			}
		</style>
	</head>

	<body>
		<div id="wrapper">
			<div id="header">
				<h1>Projekt editieren</h1>
			</div>
			<div id="site">
				${errorMsg}
				<form action="new_project" method="post">
					<table>
						<tr>
							<td>Titel</td>
							<td><input type="text" name="title" value=${title}></td>
						</tr>
						<tr>
							<td>Finanzierungslimit</td>
							<td><input type="text" name="limit" value=${limit}> €</td>
						</tr>
						<tr>
							<td>Kategorie</td>
							<td>
								<#list categories as category>
									<input type="radio" name="category" value=${category["id"]} ${category["checked"]}> ${category["name"]}<br>
								</#list>
							</td>
						</tr>
						<tr>
							<td>Vorgänger</td>
							<td>
								<input type="radio" name="pred" value="None" checked> Kein Vorgänger<br>
								<#list projects as project>
									<input type="radio" name="pred" value=${project["kennung"]} ${project["checked"]}> ${project["titel"]}<br>
								</#list>
							</td>
						</tr>
						<tr>
							<td>Beschreibung</td>
							<td><textarea name="description" cols="30" rows="5">${description}</textarea></td>
						</tr>
						<tr>
							<td></td>
							<td><button type="submit">Aktualisieren</button></td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</body>
</html>