<#-- @ftlvariable name="survey" type="com.example.model.Survey> -->
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Survey</title>
	<link rel="stylesheet" href="/static/theme.css">

	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>


</head>
<body style="text-align: center; font-family: sans-serif">
	<div class='container'>
		<div class="row">
			<div class="col-md-4"><img class="rounded-circle img-thumbnail" src="/static/logo.png" class="center"></div>
			<h2><b>Survey</b></h2>
		</div>

		<hr>
		<#list survey.questions as question>

		<div class="row">
			<div class="col-md-6 p-2"><b>${question.question}</b></div>

		        <#list question.answers as answer>

                    <div class="row">
                        <div class="col-md-5 p-2"><b>${answer.answer}</b></div>

                        <div class="col-md-1 p-2"><b>${answer.count}</b></div>
                    </div>
        		</#list>
		</div>
		</#list>
	</div>
</body>
</html>