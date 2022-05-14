For($i=0; $i -le 10000; $i++)
{
    $message="{'message':'hi$i'}"
    Write-Host "send message $message"
    mosquitto_pub.exe -h localhost -p 1883 -t meter -q 2 -i meter-recorder -m $message
    Start-Sleep -Seconds 30
}