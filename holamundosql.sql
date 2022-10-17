SELECT microservices.microservices_id,microservices.description_m,
application.app_name AS application,
application.application_id AS application_id,
layers.description AS layer,
layers.layer_id AS layer_id ,
vserver.decription AS vserver,
vserver.vserve_id AS vserver_id
FROM microservices, vserver, layers 
INNER JOIN  application ON application.application_id = layers.application_id_c 
ORDER BY microservices.microservices_id
