SELECT microservices.microservices_id,microservices.description_m,
vserver.decription AS vserver, 
vserver.vserve_id AS vserver_id, 
layers.description AS layer,
layers.layer_id AS layer_id ,
application.description AS application,
application.application_id AS application_id
FROM microservices, vserver, layers 
INNER JOIN  application ON application.application_id = layers.application_id_c 