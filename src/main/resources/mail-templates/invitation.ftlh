<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Invitation to Join</title>
</head>
<body>
<p>Dear Tenant Admin,</p>

<p>You have been invited to join our application as a Tenant Administrator.</p>

<p>Please use the following invitation token to complete your registration in the desktop application:</p>

<p><strong>${invitationToken}</strong></p>

<p>This token will expire in <span>${expiration}</span>.</p>

<p>Best regards,<br/>${officialCompanyName}</p>
</body>
</html>
