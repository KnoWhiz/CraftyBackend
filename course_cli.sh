#!/bin/bash

BASE_URL="http://localhost:8081"

function create_course() {
    echo "Creating course..."

    RESPONSE=$(curl -s -X POST "$BASE_URL/course" \
        -H "Content-Type: application/json" \
        -d "{\"topic\": \"$TOPIC\", \"type\": \"$TYPE\"}")
    # get CourseId
    COURSE_ID=$(echo "$RESPONSE" | jq -r '.course_id')

    if [ "$COURSE_ID" != "null" ] && [ -n "$COURSE_ID" ]; then
      PARAMS=$(jq -n --arg topic "$TOPIC" '{topic: $topic}')
        echo "Course created successfully with ID: $COURSE_ID"
        echo "----------------------------------------------"
        echo "💭Use the following command to generate chapter:"
        #echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step chapter --parameters '$PARAMS'"

        echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step chapter --parameters '{\"topic\": \""$TOPIC"\"}'"
        echo "----------------------------------------------"
    else
        echo "Failed to create course. Response: $RESPONSE"
    fi
    echo
}


function create_step() {
    echo "Creating step..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/step" \
    -H "Content-Type: application/json" \
    -d "{ \"courseId\": \"$COURSE_ID\", \"stepType\": \"$STEP_NAME\", \"parameters\": $PARAMS }")

    # Based on the step name, echo different messages
    case $STEP_NAME in
        "chapter")
            echo "Chapter step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate section:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step section --parameters '{\"sections_per_chapter\": 20}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step chapter"
            echo "----------------------------------------------"
            ;;
        "section")
            echo "Section step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate note:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step note --parameters '{\"max_note_expansion_words\": 500, \"chapter\": 0}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step section"
            echo "----------------------------------------------"
            ;;
        "note")
            echo "Note step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate slide:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step slide --parameters '{\"slides_template_file\": 3, \"content_slide_pages\": 30, \"chapter\": 0}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step note"
            echo "----------------------------------------------"
            echo "⏬ Use the following command to download notes and edit:"
            echo "./course_cli.sh download-files --course-id \"$COURSE_ID\" --downloadPath \"Outputs\" "
            echo "----------------------------------------------"
            ;;
        "slide")
            echo "Slide step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate script:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step script --parameters '{\"chapter\": 0}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step slide"
            echo "----------------------------------------------"
            ;;
        "script")
            echo "Script step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate voice:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step voice --parameters '{\"chapter\": 0}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step script"
            echo "----------------------------------------------"
            echo "⏬ Use the following command to download script and edit:"
            echo "./course_cli.sh download-files --course-id \"$COURSE_ID\" --downloadPath \"Outputs\" "
            echo "----------------------------------------------"
            ;;
        "voice")
            echo "Voice step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "💭Use the following command to generate video:"
            echo "./course_cli.sh create-step --course-id \"$COURSE_ID\" --step video --parameters '{\"chapter\": 0}'"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step voice"
            echo "----------------------------------------------"
            ;;
        "video")
            echo "Video step created with parameters: $PARAMS"
            echo "----------------------------------------------"
            echo "🟠Use the following command to check current step's status:"
            echo "./course_cli.sh check-status --course-id \"$COURSE_ID\" --step video"
            echo "----------------------------------------------"
            echo "⏬ Use the following command to download video and preview:"
            echo "./course_cli.sh download-files --course-id \"$COURSE_ID\" --downloadPath \"Outputs\" "
            echo "----------------------------------------------"
            echo "🎉All steps completed!"
            ;;
        *)
            echo "Unknown step name: $STEP_NAME"
            ;;
    esac

    echo "Step creation response: $RESPONSE"
    echo
}

function download_file() {
    COURSE_ID=$1
    DOWNLOAD_PATH=$2
    echo "Course ID: $COURSE_ID"
    echo "Download Path: $DOWNLOAD_PATH"
    echo "Downloading files for course ID: $COURSE_ID to $DOWNLOAD_PATH..."
    ENCODED_DOWNLOAD_PATH=$(printf '%s' "$DOWNLOAD_PATH" | jq -sRr @uri)
    RESPONSE=$(curl -s -X GET "$BASE_URL/step/download/$COURSE_ID?downloadPath=$ENCODED_DOWNLOAD_PATH" \
    -H "Content-Type: application/json")
    echo "Download response: $RESPONSE"
    echo
    }

function check_status() {
    COURSE_ID=$1
    STEP_NAME=$2
    echo "Checking status for course ID: $COURSE_ID and step: $STEP_NAME..."

    RESPONSE=$(curl -s -X GET "$BASE_URL/step/status/$COURSE_ID?stepName=$STEP_NAME")
    echo "Status response:"
        echo "$RESPONSE" | jq .


}

if [ "$1" == "create-course" ]; then
    TOPIC=$3
    TYPE=$5
    create_course
elif [ "$1" == "create-step" ]; then
    STEP_NAME=$5
    COURSE_ID=$3
    PARAMS=$7
    create_step
elif [ "$1" == "download-files" ]; then
    COURSE_ID=$3
    DOWNLOAD_PATH=$5
    download_file "$COURSE_ID" "$DOWNLOAD_PATH"
elif [ "$1" == "check-status" ]; then
    COURSE_ID=$3
    STEP_NAME=$5
    check_status "$COURSE_ID" "$STEP_NAME"
else
    echo "Invalid command. Usage: ./course_cli.sh [create-course | create-step]"
fi