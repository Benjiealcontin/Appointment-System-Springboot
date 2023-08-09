<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Appointment Notification</title>
  <style>
    body {
font-family: Arial, sans-serif;
margin: 0;
padding: 0;
display: flex;
justify-content: center;
align-items: center;
height: 100vh;
background-color: #f0f0f0;
}

.appointment-notification {
max-width: 300px;
padding: 20px;
border: 1px solid #ccc;
border-radius: 5px;
background-color: #fff;
box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

h2 {
margin-top: 0;
color: #007bff;
}

p {
margin: 8px 0;
}

strong {
font-weight: bold;
}

.button-container {
display: flex;
justify-content: space-between;
margin-top: 20px;
}

.confirm-btn {
background-color: #007bff;
}

.cancel-btn {
background-color: #dc3545;
}

.btn {
display: block;
width: 48%;
padding: 10px;
color: #fff;
border: none;
border-radius: 5px;
cursor: pointer;
margin: 5px
}

.btn:hover {
opacity: 0.8;
}
</style>
</head>

<body>
<div class="appointment-notification">
    <h2>Appointment request has been Disapprove</h2>
    <p><strong>Transaction ID:</strong> ${transactionId}</p>
    <br>
    <p>Cancel Reason: ${disapproveReason}</p>
    <br>
    <br>
    <h2>Sorry the doctor is Disapprove your Appointment Request!</h2>
  </div>
</body>
</script>
</html>