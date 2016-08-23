# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
# 
posts = Post.create([
                    {location:'세기자동차',speed:'급속',address:'인천광역시 남구 학익1동 587-71',latitude:'126.6357087',longitude:'37.4411683'},
                    {location:'인천공항 정부합동청사',speed:'급속&완속',address:'인천광역시 중구 공항로424번길 47 인천국제공항정부합동청사',latitude:'126.464624',longitude:'37.4482643'},
                    {location:'롯데마트 영종도점',speed:'급속&완속',address:'인천광역시 중구 흰바위로 51 롯데마트',latitude:'126.4878961',longitude:'37.4918654'},
                    {location:'홈플러스테스코 인하점',speed:'급속&완속',address:'인천광역시 남구 소성로 6 홈플러스테스코',latitude:'126.6741538',longitude:'37.4402656'},
                    {location:'이마트 연수점',speed:'급속&완속',address:'인천광역시 연수구 경원대로 184 이마트',latitude:'126.6901839',longitude:'37.4182654'},
                    {location:'홈플러스 구월점',speed:'급속&완속',address:'인천광역시 남동구 예술로 198 홈플러스',latitude:'126.7022637',longitude:'37.458078'},
                    {location:'한국환경공단_기후대기관',speed:'급속&완속',address:'인천광역시 서구 환경로 42 종합환경연구단지',latitude:'126.6759723',longitude:'37.5454212'},
                    {location:'인천광역시 서구청',speed:'급속&완속',address:'인천광역시 서구 서곶로 307 서구청',latitude:'126.6772296',longitude:'37.5601366'},
                    {location:'홈플러스 가좌점', speed:'급속&완속',address:'인천광역시 서구 가정로151번길 11 홈플러스',latitude:'126.6698699',longitude:'37.4979985'},
                    {location:'송도컨벤시아주차장',speed:'급속',address:'인천광역시 연수구 센트럴로 123',latitude:'126.6460025',longitude:'37.3881259'},                        
                    {location:'인천시청',speed:'급속',address:'인천광역시 남동구 정각로 29 인천시청',latitude:'126.7052611',longitude:'37.4561057'},
                    {location:'용현',speed:'완속',address:'인천시 남구 인주대로 124',latitude:'126.6508807',longitude:'37.4561283'},
                    {location:'남주안', speed:'완속',address:'인천시 남구 주안로 152',latitude:'126.6867456',longitude:'37.463445'},
                    {location:'간석', speed:'완속',address:'인천시 남동구 남동대로 938',latitude:'126.7084465',longitude:'37.4661341'}
])
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)
