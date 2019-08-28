
# Application design

* Application supports multi-types jobs.
* Slf4j used for logging jobs results. You can add any more appenders to dump results to database, Amazon S3 or CloudWatch (now results dump to file).
* The last job results stored in memory cache (via Guava). You can add easily add a handler to use database or memory database (Redis) to store there.  
* You no need to restart application to update list of all jobs. You need just send a new config:

```json
{
  "jobs": [
    {
      "type": "http",
      "url": "http://localhost:8080/api/health",
      "headers": {
        "header1": "val1"
      },
      "method": "GET",
      "status": 418,
      "contentRequirement": "OK",
      "period": "*/30 * * * * ?"
    },
    {
      "type": "http",
      "url": "https://www.facebook.com/",
      "contentRequirement": "placeholder=\"First name\"",
      "headers": {
        "Accept-Language": "en,en-GB;q=0.9"
      },
      "period": "*/20 * * * * ?"
    },
    ...
  ]
}
```
