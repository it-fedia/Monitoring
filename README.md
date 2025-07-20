# Monitoring
Настройка получения и мониторинга метрик и логов сервиса про бронированию отелей.

## Отчет по выполнению проекта Devops 9. Мониторинг

## Part 1. Получение метрик и логов

В этой главе настроил Prometheus и Loki для сбора метрик и логов приложения.


1) Скопировал нужные файлы для выполнения проекта из проекта Devops 7. Docker Swarm. Фундаментом является развернутое приложение через docker swarm на 3 хостах через Vagrant.

![nodes](images/im1-1.png)
> Список всех узлов в кластере Docker Swarm

![services](images/im1-2.png)
> Все активные сервисы

![postman](images/im1-3.png)
> Приложение работает корректно

2) Написал при помощи библиотеки Micrometer сборщики следующих метрик приложения: 
   - количество отправленных сообщений в rabbitmq;
   - количество обработанных сообщений в rabbitmq;
   - количество бронирований;
   - количество полученных запросов на gateway;
   - количество полученных запросов на авторизацию пользователей.

В сервисы booking, report, gateway и session для получения метрик нужно внести следующие изменения: 
   - Добавить зависимости в pom.xml.

   ![pom.xml](images/im1-4.png) 
   > pom.xml

   - Добавить эндпоинты приложения, по которым будут доступны метрики через HTTP.

   ![application.properties](images/im1-5.png)
   > application.properties

   - Для booking-service прописать в BookingServiceImplementation.java счетчики, отвечаюищие за количество отправленных сообщений в rabbitmq и количество бронирований.
   
   ![booking](images/im1-6.png) 
   > Внедрение счетчиков метрик

   - Для report-service прописать в файле QueueConsumer.java счетчик, отвечающий за количество обработанных сообщений в rabbitmq.

   ![report](images/im1-7.png) 
   > Внедрение счетчикa метрик

   - Для gateway-service прописать в файле MetricsFilte.java счетчик, отвечающий количество полученных запросов на gateway.

   ![gateway](images/im1-8.png) 
   > Внедрение счетчикa метрик

   - Для session-service прописать в файле SessionController.java счетчик, отвечающий за количество полученных запросов на авторизацию пользователей.

   ![session](images/im1-9.png) 
   > Внедрение счетчикa метрик

Так как образы контейнеры подтягиваются с докер хаба, собрал измененные сервисы и запушил.

![docker build](images/im1-10.png) 
> Выполнил docker build для booking-service

![docker push](images/im1-11.png) 
> Выполнил docker push для booking-service

3) Добавил логи приложения с помощью Loki.

![loki](images/im1-12.png) 
> Написал конфиг loki

![promtail](images/im1-13.png) 
> Написал конфиг экспортера promtail для loki

4) Создал новый стек для docker swarm из сервисов с Prometheus Server, Loki, node_exporter, blackbox_exporter, cAdvisor. Проверил получение метрик на порту 9090 через браузер.

![docker-compose.monitoring.yml](images/im1-14.png) 
> Написал docker-compose.monitoring.yml для стека мониторинга 

![prometheus](images/im1-15.png) 
> Написал prometheus.yml

![blackbox-exporter](images/im1-16.png) 
> Написал blackbox.yml для проверки доступности сервисов

<pre>sudo docker stack deploy -c docker-compose.monitoring.yml monitoring</pre>

![docker swarm](images/im1-17.png) 
> Запущенный стек мониторинга

Перешел на http://localhost:9090 для проверки получения метрик прометеусом. 

![prometheus](images/im1-18.png) 
> Активные targets

![prometheus](images/im1-19.png) 
> Метрики количества отправленных сообщений в rabbitmq; количества обработанных сообщений в rabbitmq; количества бронирований;

![prometheus](images/im1-20.png) 
> Метрики количества полученных запросов на gateway; количества полученных запросов на авторизацию пользователей.

![prometheus](images/im1-21.png) 
> Метрики node-exporter по нагрузке CPU.

![prometheus](images/im1-22.png) 
> Метрики cadvisor по использованию ресурсов контейнерами сервисов.

![prometheus](images/im1-23.png) 
> Метрики blackbox по доступности сервисов.

![prometheus](images/im1-24.png) 
> Логи сервисов.


## Part 2. Визуализация

В этой главе настроил Grafana для визуализации метрик и логов.

1) Развернул grafana как новый сервис в стеке мониторинга.

![docker-compose.monitoring.yml](images/im2-1.png) 
> Новый сервис grafana.

2) Добавил в grafana дашборд со следуюшими метриками:
   - количество нод;
   - количество контейнеров;
   - количество стеков;
   - использование CPU по сервисам;
   - использование CPU по ядрам и узлам;
   - затраченная RAM;
   - доступная и занятая память;
   - количество CPU;
   - доступность google.com;
   - количество отправленных сообщений в rabbitmq;
   - количество обработанных сообщений в rabbitmq;
   - количество бронирований;
   - количество полученных запросов на gateway;
   - количество полученных запросов на авторизацию пользователей;
   - логи приложения.


![grafana](images/im2-2.png) 
> Добавил в источники loki и prometheus.

![my-dashboard](images/im2-3.png) 
> Метрики количества нод, контейнеров, стеков; использование CPU по сервисам.

![my-dashboard](images/im2-4.png) 
> Метрики использования CPU по ядрам и узлам; затраченная RAM; - доступная и занятая память; количество CPU; доступность google.com;

![my-dashboard](images/im2-5.png) 
> Метрики количества отправленных сообщений в rabbitmq,обработанных сообщений в rabbitmq, бронирований, полученных запросов на gateway;

![my-dashboard](images/im2-6.png) 
> Метрики количества полученных запросов на авторизацию пользователей и логи приложения.

## Part 3. Отслеживание критических событий

В этой главе настроил Alert Manager для оповещения о критических событиях.

1) Развернул alert manager как новый сервис в стеке монтиторинга.

![docker-compose.monitoring.yml](images/im3-1.png) 
> Добавил сервис alert manager.

2) Добавил следующие критические события:
   - доступная память меньше 100 мб;
   - затраченная RAM больше 1гб;
   - использование CPU по сервису превышает 10%.

![alert_rules.yml](images/im3-3.png) 
> Написал скрипт для критических событий. 

![prometheus.yml](images/im3-4.png) 
> Прописал условия срабатывания алертов в конфиг прометеуса.

3) Настроил получение оповещений через личные email и телеграм.

![alertmanager.yml](images/im3-2.png) 
> Написал конфигурацию alert manager для отправки сообщений на почту и телеграмм бота.

![alerts](images/im3-5.png) 
> Запустившиеся алерты.

При помощи команды создавал искусственную нагрузку на узлах, чтобы проверить работоспособность оповещений.
<pre>stress --cpu 2 --vm 1 --vm-bytes 600M --vm-hang 0</pre>

![alerts](images/im3-6.png) 
> Возникновение алерта из за уменьшения свободной памяти на worker01. 

![alerts](images/im3-7.png) 
> Возникновение алертов из за увеличения потребления RAM на manager01 и worker01. 

![alerts](images/im3-8.png) 
> Возникновение алерта из за увеличения использования CPU у сервиса cadvisor.

![alerts](images/im3-9.png) 
> Оповещение об алертах на личную почту. 

![alerts](images/im3-10.png) 
> Оповещение об алертах в тг боте. 