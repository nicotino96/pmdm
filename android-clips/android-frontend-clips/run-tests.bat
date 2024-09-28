@ECHO off

call gradlew connectedLocalhostDebugAndroidTest
SetLocal EnableDelayedExpansion
SET tests_fail=unknown
type "app\build\outputs\androidTest-results\connected\flavors\localhost\TEST-Nexus_S_API_28(AVD) - 9-_app-localhost.xml" > tests_output.xml
FOR /F "tokens=4-9 delims=^= " %%i IN (tests_output.xml) DO (
  if [%%i] == [tests] (
    SET tests_total=%%j
  )
  if [%%k] == [failures] (
    SET tests_fail=%%l
  )
  if [%%m] == [errors] (
    SET tests_errors=%%n
  )
)

git show -s --format=%%H > commit_sha.temp
SET /p sha= < commit_sha.temp
SET /A tests_ok = %tests_total% - %tests_fail%
SET /A tests_ok = %tests_ok% - %tests_errors%
SET /A tests_total_int = %tests_total% + 0
if [%tests_fail%] == [unknown] (
  echo {"commit_info": "%sha%", "tests_ejecutados": "unknown", "tests_exitosos": "unknown" } > result.json
) else (
  echo {"commit_info": "%sha%", "tests_ejecutados": %tests_total_int%, "tests_exitosos": %tests_ok% } > result.json
)

