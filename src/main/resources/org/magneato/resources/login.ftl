<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    <title>Log in</title>
</head>
<body>

<div class="container">
    <h1>Login</h1>
    <div class="row">
        <div class="col-sm-4">
            <form method='POST' action='/j_security_check'>
                <input type='text' name='j_username'/>
                <input type='password' name='j_password'/>
                <input type='submit' value='Login'/>
            </form>
        </div>
<#include "/common/copyright.ftl">
        <!-- jquery -->
        <script src="/webjars/jquery/jquery.min.js"></script>
        <!-- bootstrap -->
        <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>