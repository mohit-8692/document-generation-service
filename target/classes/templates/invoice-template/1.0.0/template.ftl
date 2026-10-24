<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <style>
        body { font-family: Helvetica, Arial, sans-serif; font-size: 12px; color: #222; }
        h1 { font-size: 20px; margin-bottom: 4px; }
        table { width: 100%; border-collapse: collapse; margin-top: 16px; }
        th, td { border-bottom: 1px solid #ddd; padding: 6px 8px; text-align: left; }
        .stamp { display: inline-block; padding: 4px 10px; border-radius: 4px; font-weight: bold; }
        .stamp.paid { background: #e6f4ea; color: #1e7e34; }
        .stamp.due { background: #fdecea; color: #b3261e; }
        .total { text-align: right; font-weight: bold; margin-top: 12px; }
    </style>
</head>
<body>
    <h1>Invoice ${invoiceNumber}</h1>
    <p>Billed to: ${customerName}</p>

    <table>
        <thead>
            <tr><th>Item</th><th>Price</th></tr>
        </thead>
        <tbody>
            <#list items as item>
            <tr>
                <td>${item.name}</td>
                <td>${item.price}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <p class="total">Total: ${amount}</p>

    <#if isPaid>
        <p class="stamp paid">PAID</p>
    <#else>
        <p class="stamp due">DUE</p>
    </#if>
</body>
</html>
