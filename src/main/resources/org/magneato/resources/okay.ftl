<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css">
    <!-- jquery -->
    <script src="/webjars/jquery/jquery.min.js"></script>
    <!-- bootstrap -->
    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <title>Upload GPX</title>
</head>

<body>
<div class="container">
    <h1>Success</h1>
   Yeah!!!
    <#if value?? >
    <input type="hidden" name="parent" value="${value}">
    </#if>
</div>

<#include "/common/copyright.ftl">
</body>
</html>