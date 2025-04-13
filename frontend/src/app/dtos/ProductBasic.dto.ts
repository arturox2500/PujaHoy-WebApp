import { PublicUserDto } from './PublicUser.dto';

export interface ProductBasicDto {
  id: number;
  iniValue: number;
  name: string;
  imgURL: string;
  seller: PublicUserDto;
}