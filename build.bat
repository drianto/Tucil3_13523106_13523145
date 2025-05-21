set "PROJECT_ROOT=%~dp0"

REM bersihkan direktori bin
pushd "bin"
if errorlevel 1 (
	echo Error: Tidak dapat mengakses direktori bin untuk pembersihan awal.
	rd /s /q "%PROJECT_ROOT%bin"
	goto skip_popd_initial_clean
)
del /f /q *.* >nul 2>&1
for /d %%D in (*) do (
	rd /s /q "%%D"
)
popd
:skip_popd_initial_clean

REM compile file-file java
javac -d bin src/*.java src/controller/heuristic/*.java src/controller/solver/*.java src/controller/*.java src/model/core/*.java src/model/*.java src/utils/*.java src/view/gui/*.java

REM Membuat file jar
jar cfe bin\RushHourSolver.jar RushHourMain -C bin .

REM menghapus file-file class
pushd "bin"
for /f "delims=" %%i in ('dir /b ^| findstr /v /i /c:"RushHourSolver.jar"') do (
    if exist "%%i\" (
        rd /s /q "%%i"
    ) else (
        del /q "%%i"
    )
)