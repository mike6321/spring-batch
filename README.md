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

## Optimize

|                           | 1 time     | 2 time     | 3 time     |
| ------------------------- | ---------- | ---------- | ---------- |
| Simple Step               | 60069milis | 60777milis | 65522milis |
| Async Step                |            |            |            |
| Multi-Thread Step         |            |            |            |
| Partition Step            |            |            |            |
| Async + Partition Step    |            |            |            |
| Parallel Step             |            |            |            |
| Partition + Parallel Step |            |            |            |

```sql
delete from orders where 1=1;
delete from users where 1=1;
delete from person where 1=1;
```
