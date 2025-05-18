# 设置控制台编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "正在检查环境..." -ForegroundColor Green

# 检查 Java 环境
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java 环境检查通过" -ForegroundColor Green
} catch {
    Write-Host "错误：未找到 Java 环境，请安装 JDK" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

# 检查 Maven 环境
$mavenHome = "C:\Program Files\Apache\maven\apache-maven-3.9.9"
if (-not (Test-Path $mavenHome)) {
    Write-Host "错误：Maven 目录不存在：$mavenHome" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

$mvnCmd = Join-Path $mavenHome "bin\mvn.cmd"
if (-not (Test-Path $mvnCmd)) {
    Write-Host "错误：未找到 Maven 命令：$mvnCmd" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "环境检查通过，正在启动服务..." -ForegroundColor Green

# 切换到项目目录
Set-Location $PSScriptRoot

# 检查 pom.xml 是否存在
if (-not (Test-Path "pom.xml")) {
    Write-Host "错误：未找到 pom.xml 文件" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "正在编译项目..." -ForegroundColor Green
& $mvnCmd clean install -Dfile.encoding=UTF-8
if ($LASTEXITCODE -ne 0) {
    Write-Host "错误：项目编译失败" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "正在启动服务..." -ForegroundColor Green
& $mvnCmd spring-boot:run -Dfile.encoding=UTF-8

Read-Host "按回车键退出" 
Write-Host "脚本执行完毕，按任意键退出..."
[void][System.Console]::ReadKey($true)