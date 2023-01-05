package com.example.CustomerSE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.Queue;

public class x {

    Logger logger = (Logger) LoggerFactory.getLogger(x.class);
    @CrossOrigin
    @GetMapping(value = "/getQueuesStreamByObject",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ResponseEntity<?>>stream2(){
        return Flux.interval(Duration.ofSeconds(20))
                .map(x -> {
                    try {
                        return this.getdepthQueueStream();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                });
    }

    public synchronized ResponseEntity<String> getdepthQueueStream() throws Exception
    {
        try {
            logger.debug("Generando listado de Colas PIC ");

            if(lastUpdateTime != null &&  (new Date().getTime() - lastUpdateTime.getTime() ) < 10*1000 )
            {
                logger.debug("Utiliza Cache para generar html");
            }else
            {
                queueDetail = new ArrayList<QueueDetail>();
                instances= queueMonitor.getInstances();

                queueList = queueMonitor.deepQueue(false);
                queueListResp = queueMonitor.deepQueue(true);
                int queueIdx=0;
                totalTransxSec = 0;
                totalInstancesRead = 0 ;
                for(Queue queue: queueList)
                {
                    QueueDetail detail = new QueueDetail();
                    detail.setResquestSecond(0);
                    detail.setInstanceList(new HashMap<String,Instance>());
                    detail.setName(queue.getName());
                    detail.setMaxDepthRq(queue.getMaximumDepth());
                    detail.setCurrentDepthRq(queue.getCurrentDepth());
                    detail.setInputRq(queue.getOpenInputCount());
                    detail.setOutputRq(queue.getOpenOutputCount());

                    detail.setMaxDepthResp(queueListResp.get(queueIdx).getMaximumDepth());
                    detail.setCurrentDepthResp(queueListResp.get(queueIdx).getCurrentDepth());
                    detail.setInputResp(queueListResp.get(queueIdx).getOpenInputCount());
                    detail.setOutputResp(queueListResp.get(queueIdx).getOpenOutputCount());

                    for(String instancia: instances.keySet())
                    {
                        if(instances.get(instancia) != null && queue.getName().contains(instances.get(instancia).getQueueName()))
                        {
                            if(queueDetailOld != null)
                            {
                                for(QueueDetail queueOld : queueDetailOld)
                                {
                                    Instance oldInstance=queueOld.getInstanceList().get(instances.get(instancia).getName());
                                    if(oldInstance != null)
                                    {
                                        long seconds = (instances.get(instancia).getUpdateTime().getTime()
                                                - oldInstance.getUpdateTime().getTime()
                                        )/1000;
                                        float totalRequest = instances.get(instancia).getSuccess() - oldInstance.getSuccess();
                                        instances.get(instancia).setRequestSec(totalRequest/seconds);
                                        detail.setResquestSecond(detail.getResquestSecond()+instances.get(instancia).getRequestSec());
                                    }
                                }
                            }
                            detail.getInstanceList().put(instances.get(instancia).getName(), instances.get(instancia));
                        }
                    };
                    queueDetail.add(detail);
                    totalTransxSec = totalTransxSec + detail.getResquestSecond();
                    totalInstancesRead = totalInstancesRead + detail.getInputResp();
                    queueIdx++;
                }
                queueDetailOld = queueDetail;
                lastUpdateTime = new Date();
            }



            queueDetail.sort(Comparator.comparing(QueueDetail::getResquestSecond).reversed());

            ObjectMapper objectMapper= new ObjectMapper();

            logger.debug("Html generado ");
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(queueDetail));
        }catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }

        @GetMapping(value = "/getNames",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<ResponseEntity<List<Application>>>stream2() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(x -> {
                    try {
                        return this.applicationName.GetNamesStream();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return null;
                    }
                });
    }
    }
}
