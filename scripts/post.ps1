
$json = Get-Content $args[0] -Raw

# Make the POST request
$response = Invoke-RestMethod -Uri 'http://127.0.0.1/api/survey/create' -Method POST -Body $json -ContentType 'application/json'

# Output the response
$response