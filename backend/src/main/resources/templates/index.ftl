<#-- @ftlvariable name="entries" type="kotlin.collections.List<com.example.model.Survey>" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Survey</title>
	<link rel="stylesheet" href="/static/theme.css">
</head>
<body style="text-align: center; font-family: sans-serif">
<img src="/static/logo.png">
<h1>Survey</h1>
<p><i>Powered by Ktor, kotlinx.html & Freemarker!</i></p>
<hr>
<#list entries as item>
    <div>
        <h3>${item.id}</h3>
        <p><a href='/results?id=${item.id}'</a>${item.name}</p>
    </div>
</#list>
<hr>
<div>
    <h3>Add a new journal entry!</h3>
    <form action="/submit" method="post">
        <input type="text" name="headline">
        <br>
        <textarea name="body"></textarea>
        <br>
        <input type="submit">
    </form>
</div>
</body>
</html>