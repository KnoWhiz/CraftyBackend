## Setting Up the Environment
#### 1. Build the Project
   
   Ensure you are in the project directory that build.gradle is existed, then run:

```
gradle build
```
#### 2. Run the Backend Service

```
gradle bootRun
```
## CLI Tool Usage
The CLI tool provides various commands to interact with the Crafty backend. Below are instructions on how to use each command.

### 1. Check Status
  Check the status of a task for a specific course and step.

```
./course_cli.sh check-status --course-id <COURSE_ID> --step <STEP_NAME>
```
Example:
```
./course_cli.sh check-status --course-id "66d916dd958f640cfc390312" --step chapter
```
### 2. Download Files
Download files associated with a course to the project folder.

```
./course_cli.sh download-file --course-id <COURSE_ID> --path <PATH>
```
Example:
```
./course_cli.sh download-file --course-id "1234567" --path "Result"
```
### 3. Upload Files
Upload the folder to Azure Blob Storage under a specified course ID after editing.

```
./course_cli.sh upload-file --course-id <COURSE_ID> --upload-path "Result"
```
Example:

```
./course_cli.sh upload-file --course-id "66dba1c93de2577a5faaefb8" --upload-path "Outputs"
```

