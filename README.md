# spring-batch

Spring Batch 스터디를 위한 repository 입니다.

* [Job](https://jwdeveloper.notion.site/Job-8eb1e879e1e14dbd90d52eb7508d9811)
* [JobExecution](https://jwdeveloper.notion.site/JobExecution-71703481fa5846d5bb9d84b6b05a0a45)
* [Step](https://jwdeveloper.notion.site/Step-060f839bbd3b444ab35bfb27df2c632f)

## Program arguments

* -chunkSize=20 --job.name=chunkProcessingJob
* --job.name=itemReaderJob
* --job.name=savePersonJob
* -allow_duplicate=false --job.name=savePersonJob
* --job.name=userJob
* -date=2022-10 --job.name=userJob
* --job.name=userJob
* -date=2022-11 --job.name=userJob
* -date=2022-11 --job.name=asyncUserJob
* -date=2022-11 --job.name=multiThreadUserJob
* -date=2022-11 --job.name=partitionUserJob
* -date=2022-11 --job.name=parallelUserJob
*  mvn clean package -Dmaven.test.skip=true

## Command

*  java -jar spring-batch-0.0.1-SNAPSHOT.jar --job.name=userJob -date=2022-11 -path=/Users/nhn/IdeaProjects/repository/spring-batch/output/

## Optimize

|                           | 1 time     | 2 time     | 3 time     |
| ------------------------- | ---------- | ---------- | ---------- |
| Simple Step               | 60069milis | 60777milis | 65522milis |
| Async Step                | 64493milis | 59089milis | 60021milis |
| Multi-Thread Step         | 37461milis | 36840milis | 43474milis |
| Partition Step            | 48104milis | 42328milis | 42126milis |
| Async + Partition Step    | 42979milis | 41694milis | 41428milis |
| Parallel Step             | 62149milis | 61029milis | 62029milis |
| Partition + Parallel Step | 44646milis | 41855milis | 44784milis |
