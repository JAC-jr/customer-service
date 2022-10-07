SELECT application.app_name,layers.description AS layer
FROM application INNER join layers 
ON layers."application_ID" = application.application_id
where application.app_name='BDVonline'