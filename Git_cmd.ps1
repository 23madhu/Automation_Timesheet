git init
git add .
git commit -m "Initial commit"

git branch -M main

if (!(git remote | Select-String "origin")) {
    git remote add origin https://github.com/23madhu/Automation_Timesheet.git
}

git push -u origin main