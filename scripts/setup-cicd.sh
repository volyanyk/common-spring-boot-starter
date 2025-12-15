#!/bin/bash

# setup-cicd.sh
# Usage: ./setup-cicd.sh [github|gitlab|jenkins]

PROVIDER=$1

if [ -z "$PROVIDER" ]; then
    echo "Usage: ./setup-cicd.sh [github|gitlab|jenkins]"
    exit 1
fi

case $PROVIDER in
    github)
        mkdir -p .github/workflows
        cp templates/github-actions.yml .github/workflows/maven.yml
        echo "GitHub Actions workflow configured in .github/workflows/maven.yml"
        ;;
    gitlab)
        cp templates/gitlab-ci.yml .gitlab-ci.yml
        echo "GitLab CI configured in .gitlab-ci.yml"
        ;;
    jenkins)
        cp templates/jenkins/Jenkinsfile Jenkinsfile
        echo "Jenkinsfile configured in Jenkinsfile"
        ;;
    *)
        echo "Invalid provider: $PROVIDER"
        echo "Supported providers: github, gitlab, jenkins"
        exit 1
        ;;
esac
