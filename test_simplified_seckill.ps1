# test_simplified_seckill.ps1
# 自动启动微服务并测试简化后的三种秒杀锁方案 (Synchronized, Redisson, Zookeeper)

# 请在运行前确保已在 Clash 代理软件中设置局域网直连 (Bypass: 192.168.184.0/24) 或暂时关闭 Clash 代理，否则会导致中间件连接超时。

$ErrorActionPreference = "Stop"

# 1. 查找 Java 路径
$javaPath = "C:\Program Files\Java\jdk-21\bin\java.exe"
if (-not (Test-Path $javaPath)) {
    $javaPath = "java"
}

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "1. 启动 goodskill-service (核心提供者) ..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$serviceJar = "E:\goodskill_project\goodsKill\goodskill-seckill-provider\goodskill-service\target\goodskill-service.jar"
$serviceProc = Start-Process -FilePath $javaPath -ArgumentList "-jar", "`"$serviceJar`"" -PassThru -NoNewWindow

Write-Host "goodskill-service PID: $($serviceProc.Id)" -ForegroundColor Green
Write-Host "等待 25 秒让提供者完成注册和初始化..." -ForegroundColor Yellow
Start-Sleep -Seconds 25

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "2. 启动 goodskill-web (主控服务) ..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$webJar = "E:\goodskill_project\goodsKill\goodskill-web\target\goodskill.jar"
$webProc = Start-Process -FilePath $javaPath -ArgumentList "-jar", "`"$webJar`"" -PassThru -NoNewWindow

Write-Host "goodskill-web PID: $($webProc.Id)" -ForegroundColor Green
Write-Host "等待 web 服务加载端口 8082 (最长等待 40 秒)..." -ForegroundColor Yellow

$portOpened = $false
for ($i = 0; $i -lt 20; $i++) {
    try {
        $connection = Test-NetConnection -ComputerName "localhost" -Port 8082 -InformationLevel Quiet -ErrorAction SilentlyContinue
        if ($connection) {
            $portOpened = $true
            break
        }
    } catch {}
    Start-Sleep -Seconds 2
}

if (-not $portOpened) {
    Write-Error "web 服务在 40 秒内未成功开启 8082 端口，请检查后台日志！"
    exit 1
}

Write-Host "web 服务启动完毕，开始发送模拟秒杀请求进行功能验证..." -ForegroundColor Green

# 3. 发送请求测试
$headers = @{ "Content-Type" = "application/json" }
$body = '{"seckillId":1000,"seckillCount":10,"requestCount":100}'

Write-Host "`n[测试方案一] 发送同步锁秒杀请求 (/sychronized) ..." -ForegroundColor Cyan
try {
    $res1 = Invoke-RestMethod -Uri "http://localhost:8082/sychronized" -Method Post -Headers $headers -Body $body
    Write-Host "返回结果: " -NoNewline
    $res1 | ConvertTo-Json | Write-Host -ForegroundColor Green
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
}

Write-Host "`n[测试方案二] 发送 Redisson 分布式锁秒杀请求 (/redisson) ..." -ForegroundColor Cyan
try {
    $res2 = Invoke-RestMethod -Uri "http://localhost:8082/redisson" -Method Post -Headers $headers -Body $body
    Write-Host "返回结果: " -NoNewline
    $res2 | ConvertTo-Json | Write-Host -ForegroundColor Green
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
}

Write-Host "`n[测试方案三] 发送 Zookeeper 分布式锁秒杀请求 (/zookeeperLock) ..." -ForegroundColor Cyan
try {
    $res3 = Invoke-RestMethod -Uri "http://localhost:8082/zookeeperLock" -Method Post -Headers $headers -Body $body
    Write-Host "返回结果: " -NoNewline
    $res3 | ConvertTo-Json | Write-Host -ForegroundColor Green
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
}

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "测试流程结束！" -ForegroundColor Cyan
Write-Host "若要停止运行的服务，请在命令行中执行: Stop-Process -Id $($serviceProc.Id), $($webProc.Id) -Force" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
