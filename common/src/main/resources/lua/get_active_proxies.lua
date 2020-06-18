local liveKeys = redis.call("KEYS", "proxy:heartbeats:*")
local proxyIds = {}

for _, value in pairs(liveKeys) do
    local proxyId = string.gsub(value, "proxy:heartbeats:", "")
    table.insert(proxyIds, proxyId)
end

return proxyIds