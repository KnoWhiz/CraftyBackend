## CLI Instruction
The CLI tool provides various commands to interact with the Crafty backend. Below are instructions on how to use each command.

### create-course (First Step)
  Create a new entry in course database from given ApiKey, topic and course type.


```
./course_cli.sh create-course <APIKEY> -topic <TOPIC> --type <TYPE>
```
Example:
```
./course_cli.sh create-course "xxxx" -topic "I want to learn Chinese history" --type "short"
```
### create_step 💭
The step command is used to execute a specific step in the course creation process. The steps should be executed in the following order:

1. `chapter`
2. `section`
3. `note`
4. `slide`
5. `script`
6. `voice`
7. `video`

#### Chapter
You should always start with chapter command to create meta data and chapters for a given learning topic.

After running `create-course`, the terminal will show up the following command for `chapter`.

```
./course_cli.sh create-step --course-id <COURSE_ID> --step chapter --parameters '{"topic": <TOPIC>}'
```
#### Section
Follow the terminal and run the command for `section`.

```
./course_cli.sh create-step --course-id <COURSE_ID> --step section --parameters '{"sections_per_chapter": 20}'
```
`--sections_per_chapter` is the number of sections you want to create for each chapter. The default value is 20.

#### Note
Follow the terminal and run the command for `note`.

```
./course_cli.sh create-step --course-id <COURSE_ID> --step note --parameters '{"max_note_expansion_words": 500, "chapter": 0}'
```
`--max_note_expansion_words` is the maximum number of words to expand the notes. The default value is 500.

`--chapter` is the chapter index to generate notes for. The chapter number start from 0.

#### Slide
Follow the terminal and run the command for `slide`.

```
./course_cli.sh create-step --course-id <COURSE_ID> --step slide --parameters '{"slides_template_file": 3, "content_slide_pages": 30, "chapter": 0}'
```
`--slides_template_file` is the template file to use for generating slides. The default value is 3.

`--content_slide_pages` is the number of pages to generate for content slides. The default value is 30.

#### Script
Follow the terminal and run the command for `script`.

```
./course_cli.sh create-step --course-id <COURSE_ID> --step script --parameters '{"chapter": 0}'
```

#### Voice
Follow the terminal and run the command for `voice`.
```
./course_cli.sh create-step --course-id <COURSE_ID> --step voice --parameters '{"chapter": 0}'
```

#### Video (Might take hours to run)
Follow the terminal and run the command for `video`.
```
./course_cli.sh create-step --course-id <COURSE_ID> --step video --parameters '{"chapter": 0}'
```
### check-status 🟠
Check the status of a task for a specific course and step.

**The following command can be found in terminal after each step.**

```
./course_cli.sh check-status --course-id <COURSE_ID> --step <STEP_NAME>

# "status": "completed": the step you ran was successful.
# "status": "failed": the step you ran failed.
```

### download-file ⏬
Download course zip file to a specified local directory and feel free to edit them.

<mark> we only support `note` and `script` steps editing. </mark>

**The following command can be found in terminal after `note`, `script` and `video` steps.**
```
./course_cli.sh download-file --course-id <COURSE_ID> --downloadPath "Outputs"
```


### Upload-file ⬆️
Upload the zip file to Azure Blob Storage under a specified course ID <mark>after editing</mark>.
```
./course_cli.sh upload-file --course-id <COURSE_ID> -file "Outputs/xxxx.zip"
```


