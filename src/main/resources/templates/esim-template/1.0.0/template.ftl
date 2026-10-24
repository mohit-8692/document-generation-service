<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <style>
        body { font-family: Helvetica, Arial, sans-serif; font-size: 12px; color: #222; }
        h1 { font-size: 20px; }
    </style>
</head>
<body>
    <h1>${packageName}</h1>
    <p>ICCID: ${iccid}</p>
    <#if qrCodeUrl??>
        <img src="${qrCodeUrl}" width="140" height="140"/>
    </#if>
</body>
</html>
