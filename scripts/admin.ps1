$admin = Read-Host "Enter Admin account"

# Construct the JSON object with the word
$jsonData = @{
    "account" = $admin
} | ConvertTo-Json

# Define the endpoint URL
$endpoint = "http://127.0.0.1/api/admin/set"

# Make the POST request
$response = Invoke-RestMethod -Uri $endpoint -Method Post -Body $jsonData -ContentType 'application/json'

# Display the response
$response